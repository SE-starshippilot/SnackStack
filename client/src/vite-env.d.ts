interface ImportMetaEnv {
  VITE_TEST_EMAIL: any;
  VITE_TEST_USERNAME: any;
  VITE_DISABLE_AUTH: string;
  VITE_CLERK_PUBLISHABLE_KEY: any;
  VITE_APP_NODE_ENV: string;
}

interface ImportMeta {
  env: ImportMetaEnv;
}
