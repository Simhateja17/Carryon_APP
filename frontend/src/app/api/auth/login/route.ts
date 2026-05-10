import { NextRequest, NextResponse } from "next/server";
import { timingSafeEqual } from "crypto";
import { buildSessionCookie } from "@/lib/adminSession";

// In-memory rate limiter keyed by IP
const attempts = new Map<string, { count: number; resetAt: number }>();
const MAX_ATTEMPTS = 5;
const WINDOW_MS = 15 * 60 * 1000; // 15 minutes
const LOCKOUT_MS = 30 * 60 * 1000; // 30 minutes after max attempts

function getRateLimit(ip: string): { allowed: boolean; retryAfterMs: number } {
  const now = Date.now();
  const entry = attempts.get(ip);

  if (!entry || now > entry.resetAt) {
    attempts.set(ip, { count: 1, resetAt: now + WINDOW_MS });
    return { allowed: true, retryAfterMs: 0 };
  }

  if (entry.count >= MAX_ATTEMPTS) {
    const retryAfterMs = entry.resetAt - now;
    return { allowed: false, retryAfterMs };
  }

  entry.count++;
  return { allowed: true, retryAfterMs: 0 };
}

function recordFailure(ip: string) {
  const entry = attempts.get(ip);
  if (entry && entry.count >= MAX_ATTEMPTS) {
    // Extend lockout window on reaching max
    entry.resetAt = Date.now() + LOCKOUT_MS;
  }
}

function clearAttempts(ip: string) {
  attempts.delete(ip);
}

export async function POST(request: NextRequest) {
  const ip =
    request.headers.get("x-forwarded-for")?.split(",")[0]?.trim() ||
    request.headers.get("x-real-ip") ||
    "unknown";

  const rateLimit = getRateLimit(ip);
  if (!rateLimit.allowed) {
    const retryAfter = Math.ceil(rateLimit.retryAfterMs / 1000);
    return NextResponse.json(
      { success: false, message: "Too many login attempts. Try again later." },
      {
        status: 429,
        headers: { "Retry-After": String(retryAfter) },
      }
    );
  }

  const adminPassword = process.env.ADMIN_PASSWORD;
  if (!adminPassword) {
    return NextResponse.json(
      { success: false, message: "Admin login is not configured" },
      { status: 503 }
    );
  }

  let body: { password?: string };
  try {
    body = await request.json();
  } catch {
    return NextResponse.json(
      { success: false, message: "Invalid request body" },
      { status: 400 }
    );
  }

  const provided = body.password || "";
  if (
    provided.length !== adminPassword.length ||
    !timingSafeEqual(Buffer.from(provided), Buffer.from(adminPassword))
  ) {
    recordFailure(ip);
    return NextResponse.json(
      { success: false, message: "Invalid credentials" },
      { status: 401 }
    );
  }

  clearAttempts(ip);
  const cookie = buildSessionCookie();
  const response = NextResponse.json({ success: true });
  response.cookies.set(cookie.name, cookie.value, cookie.options as never);
  return response;
}
