interface ImportMetaEnv {
  VITE_API_BASE_URL: any;
  VITE_DISABLE_AUTH: string;
  VITE_CLERK_PUBLISHABLE_KEY: any;
  VITE_APP_NODE_ENV: string;
}

interface ImportMeta {
  env: ImportMetaEnv;
}
