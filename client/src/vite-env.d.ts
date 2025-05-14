interface ImportMetaEnv {
  VITE_DISABLE_AUTH: string;
  VITE_CLERK_PUBLISHABLE_KEY: any;
  VITE_APP_NODE_ENV: string;
}

interface ImportMeta {
  env: ImportMetaEnv;
}
