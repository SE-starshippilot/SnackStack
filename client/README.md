# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.


# Clerk authentication setup
You must add a Clerk publishable key before running locally
1) Copy the example file oto your own local env file:
cp .env.example .env.local
2) Replace the place holder with your own value (VITE_CLERK_PUBLISHABLE_KEY=pk_XXXXXXXXXXXXXXXXXXXX)
You can use your own, or you are also welcome to use mine (VITE_CLERK_PUBLISHABLE_KEY=pk_test_YW1wbGUtZmVycmV0LTIzLmNsZXJrLmFjY291bnRzLmRldiQ)
3) For testing purposes skip Clerk authentication by adding this line to your .env.local VITE_DISABLE_AUTH=true
4) Mock user password/email:
E2E_CLERK_USER_USERNAME=cat@brown.edu
E2E_CLERK_USER_PASSWORD=meowfish123