import { NextRequest, NextResponse } from "next/server";

const PRODUCTION_URL = "https://api.carryon.my";

function backendBaseUrl() {
  return (
    process.env.API_URL ||
    process.env.NEXT_PUBLIC_API_URL ||
    PRODUCTION_URL
  ).replace(/\/$/, "");
}

async function proxyAdminRequest(
  request: NextRequest,
  context: { params: Promise<{ path: string[] }> }
) {
  const adminKey = process.env.ADMIN_API_KEY;
  if (!adminKey) {
    return NextResponse.json(
      { success: false, message: "Admin access is not configured" },
      { status: 503 }
    );
  }

  const params = await context.params;
  if (params.path.some((segment) => segment === "." || segment === "..")) {
    return NextResponse.json(
      { success: false, message: "Invalid admin path" },
      { status: 400 }
    );
  }

  const path = params.path.join("/");
  const targetUrl = new URL(`/api/admin/${path}`, backendBaseUrl());
  if (!targetUrl.pathname.startsWith("/api/admin/")) {
    return NextResponse.json(
      { success: false, message: "Invalid admin path" },
      { status: 400 }
    );
  }
  targetUrl.search = request.nextUrl.search;

  const headers = new Headers(request.headers);
  headers.set("x-admin-key", adminKey);
  headers.delete("host");
  headers.delete("content-length");

  const response = await fetch(targetUrl, {
    method: request.method,
    headers,
    body: ["GET", "HEAD"].includes(request.method)
      ? undefined
      : await request.arrayBuffer(),
    cache: "no-store",
  });

  const responseHeaders = new Headers(response.headers);
  responseHeaders.delete("content-encoding");
  responseHeaders.delete("content-length");

  return new NextResponse(response.body, {
    status: response.status,
    statusText: response.statusText,
    headers: responseHeaders,
  });
}

export const GET = proxyAdminRequest;
export const POST = proxyAdminRequest;
export const PUT = proxyAdminRequest;
export const DELETE = proxyAdminRequest;
