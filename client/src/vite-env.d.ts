interface ImportMetaEnv {
  VITE_DISABLE_AUTH: string;
  VITE_CLERK_PUBLISHABLE_KEY: any;
  VITE_APP_NODE_ENV: string;
  // define more env variables if needed
}

interface ImportMeta {
  env: ImportMetaEnv;
}
