import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  eslint: {
    // Warning: This allows production builds to successfully complete even if
    // your project has ESLint errors.
    ignoreDuringBuilds: true,
  },
  typescript: {
    // Similarly, ignore type errors during build for speed and stability on Vercel
    ignoreBuildErrors: true,
  }
};

export default nextConfig;
