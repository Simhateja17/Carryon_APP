import { cookies } from "next/headers";
import { createHmac, timingSafeEqual } from "crypto";

const COOKIE_NAME = "carryon_admin_session";
const SESSION_MAX_AGE = 8 * 60 * 60; // 8 hours

function getSessionSecret(): string {
  const secret = process.env.ADMIN_SESSION_SECRET;
  if (!secret) throw new Error("ADMIN_SESSION_SECRET is not configured");
  return secret;
}

function sign(payload: string): string {
  return createHmac("sha256", getSessionSecret()).update(payload).digest("hex");
}

function createSessionToken(): string {
  const expiresAt = Math.floor(Date.now() / 1000) + SESSION_MAX_AGE;
  const payload = `admin:${expiresAt}`;
  const signature = sign(payload);
  return `${payload}.${signature}`;
}

function verifySessionToken(token: string): boolean {
  const lastDot = token.lastIndexOf(".");
  if (lastDot === -1) return false;

  const payload = token.substring(0, lastDot);
  const signature = token.substring(lastDot + 1);

  const expectedSig = sign(payload);
  if (signature.length !== expectedSig.length) return false;

  const valid = timingSafeEqual(
    Buffer.from(signature, "hex"),
    Buffer.from(expectedSig, "hex")
  );
  if (!valid) return false;

  const parts = payload.split(":");
  if (parts.length !== 2 || parts[0] !== "admin") return false;

  const expiresAt = parseInt(parts[1], 10);
  if (Number.isNaN(expiresAt) || expiresAt < Math.floor(Date.now() / 1000)) {
    return false;
  }

  return true;
}

export async function verifyAdminSession(): Promise<boolean> {
  try {
    const cookieStore = await cookies();
    const session = cookieStore.get(COOKIE_NAME);
    if (!session?.value) return false;
    return verifySessionToken(session.value);
  } catch {
    return false;
  }
}

export function buildSessionCookie(): {
  name: string;
  value: string;
  options: Record<string, unknown>;
} {
  return {
    name: COOKIE_NAME,
    value: createSessionToken(),
    options: {
      httpOnly: true,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      path: "/",
      maxAge: SESSION_MAX_AGE,
    },
  };
}

export { COOKIE_NAME };
