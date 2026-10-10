Here's the complete setup guide as a single reference file. Save it as `SETUP.md` in your repo root or keep it in your notes.

```markdown
# rentrix Client — Project Setup Guide

Stack: Vite + React + TypeScript + Tailwind v4 + shadcn/ui (radix-nova, zinc, lucide)
State: Zustand | Server: TanStack Query + Axios | Forms: RHF + Zod | Router: React Router v7

---

## Prerequisites

- Node 18.18+ (20+ preferred)
- npm 9+

---

## Step 1 — Create Vite project

```bash
npm create vite@latest rentrix-client -- --template react-ts
cd rentrix-client
npm install
npm run dev
```

Verify: `http://localhost:5173` loads the default Vite page.

---

## Step 2 — Tailwind v4 + shadcn/ui

### 2.1 Install Tailwind Vite plugin

```bash
npm i -D @tailwindcss/vite
```

### 2.2 Replace `src/index.css`

```css
@import "tailwindcss";
```

### 2.3 Update `vite.config.ts`

```ts
import path from "path"
import tailwindcss from "@tailwindcss/vite"
import react from "@vitejs/plugin-react"
import { defineConfig } from "vite"

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
})
```

> The proxy lets you hit `/api/v1/...` in dev without CORS on the backend.

### 2.4 Install `@types/node`

```bash
npm i -D @types/node
```

### 2.5 Path alias — add to BOTH `tsconfig.json` and `tsconfig.app.json`

```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

### 2.6 Install runtime deps

```bash
npm i react-router-dom @tanstack/react-query axios zustand \
      react-hook-form zod @hookform/resolvers \
      lucide-react sonner date-fns clsx tailwind-merge \
      class-variance-authority
```

### 2.7 Install dev deps

```bash
npm i -D @tanstack/react-query-devtools \
         prettier prettier-plugin-tailwindcss \
         eslint-config-prettier tw-animate-css
```

### 2.8 Init shadcn

```bash
npx shadcn@latest init
```

Recommended answers (shadcn v4.21+):
- Style: `radix-nova` (Radix UI primitives, Nova compact style)
- Base color: `zinc`
- CSS variables: yes
- Icon library: `lucide`

> Other v4 styles: vega, nova, maia, lyra, mira, luma, sera, rhea.
> Only `radix-*` styles use Radix UI primitives (best documented).

### 2.9 Add base components

```bash
npx shadcn@latest add button input label card form dialog \
  dropdown-menu avatar badge select textarea sonner \
  table skeleton separator
```

### 2.10 Verify `components.json`

Should look like:

```json
{
  "$schema": "https://ui.shadcn.com/schema.json",
  "style": "radix-nova",
  "rsc": false,
  "tsx": true,
  "tailwind": {
    "config": "",
    "css": "src/index.css",
    "baseColor": "zinc",
    "cssVariables": true,
    "prefix": ""
  },
  "iconLibrary": "lucide",
  "rtl": false,
  "aliases": {
    "components": "@/components",
    "utils": "@/lib/utils",
    "ui": "@/components/ui",
    "lib": "@/lib",
    "hooks": "@/hooks"
  },
  "menuColor": "default",
  "menuAccent": "subtle",
  "registries": {}
}
```

### 2.11 Fix shadcn CLI bug (if present)

Sometimes generated components import `cn` from `"cn"` instead of `"@/lib/utils"`.
Check `src/components/ui/button.tsx`:

```tsx
import { cn } from "@/lib/utils"   // ✅ correct
// import { cn } from "cn"          // ❌ fix this
```

### 2.12 Verify

```bash
npm run dev
```

Checklist:
- [ ] Dev server runs
- [ ] `src/index.css` has theme variables (from shadcn init)
- [ ] `components.json` exists with correct values
- [ ] `src/lib/utils.ts` has `cn()`
- [ ] `src/components/ui/button.tsx` exists

---

## Step 3 — Tooling, env, and folder skeleton

### 3.1 Prettier

```bash
npm i -D prettier prettier-plugin-tailwindcss
```

`.prettierrc`:

```json
{
  "semi": false,
  "singleQuote": true,
  "trailingComma": "all",
  "printWidth": 100,
  "tabWidth": 2,
  "plugins": ["prettier-plugin-tailwindcss"]
}
```

`.prettierignore`:

```
node_modules
dist
build
coverage
*.md
.env
.env.*
```

### 3.2 Update `package.json` scripts

```json
"scripts": {
  "dev": "vite",
  "build": "tsc -b && vite build",
  "lint": "eslint .",
  "lint:fix": "eslint . --fix",
  "format": "prettier --write .",
  "format:check": "prettier --check .",
  "preview": "vite preview"
}
```

### 3.3 ESLint

```bash
npm i -D eslint-config-prettier
```

Replace `eslint.config.js`:

```js
import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import prettier from 'eslint-config-prettier'

export default tseslint.config(
  { ignores: ['dist', 'node_modules'] },
  {
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2022,
      globals: globals.browser,
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
      '@typescript-eslint/no-unused-vars': [
        'warn',
        { argsIgnorePattern: '^_', varsIgnorePattern: '^_' },
      ],
    },
  },
  prettier,
)
```

Verify: `npm run lint` and `npm run format`.

### 3.4 Env files

`.env.example` (committed):

```env
VITE_API_BASE_URL=/api/v1
VITE_APP_NAME=rentrix
VITE_ENABLE_MOCKS=true
```

`.env` (gitignored — same content as above).

Ensure `.gitignore` has:

```
.env
.env.local
.env.*.local
```

### 3.5 Env validation — `src/lib/env.ts`

```ts
import { z } from 'zod'

const envSchema = z.object({
  VITE_API_BASE_URL: z.string().min(1, 'VITE_API_BASE_URL is required'),
  VITE_APP_NAME: z.string().min(1, 'VITE_APP_NAME is required'),
  VITE_ENABLE_MOCKS: z
    .enum(['true', 'false'])
    .default('false')
    .transform((v) => v === 'true'),
})

const parsed = envSchema.safeParse(import.meta.env)

if (!parsed.success) {
  console.error('❌ Invalid environment variables:')
  console.error(parsed.error.flatten().fieldErrors)
  throw new Error('Invalid environment variables')
}

export const env = parsed.data
```

Use `env.VITE_API_BASE_URL` everywhere — never `import.meta.env` directly.

### 3.6 Folder skeleton

```bash
mkdir -p src/api/mock
mkdir -p src/components/layout
mkdir -p src/components/common
mkdir -p src/features/auth/components
mkdir -p src/features/auth/hooks
mkdir -p src/features/flats/components
mkdir -p src/features/flats/hooks
mkdir -p src/features/reviews/components
mkdir -p src/features/reviews/hooks
mkdir -p src/pages
mkdir -p src/routes
mkdir -p src/types
mkdir -p src/hooks
```

Final structure:

```
src/
├─ api/
│  └─ mock/
├─ components/
│  ├─ ui/           (shadcn)
│  ├─ layout/
│  └─ common/
├─ features/
│  ├─ auth/{components,hooks}/
│  ├─ flats/{components,hooks}/
│  └─ reviews/{components,hooks}/
├─ hooks/
├─ lib/
│  ├─ utils.ts
│  └─ env.ts
├─ pages/
├─ routes/
├─ types/
├─ App.tsx
├─ main.tsx
└─ index.css
```

### 3.7 Clean up scaffold

Delete:
- `src/App.css`
- `src/assets/react.svg`

Replace `src/App.tsx`:

```tsx
export default function App() {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <h1 className="text-2xl font-bold">rentrix — setup in progress</h1>
    </div>
  )
}
```

Update `src/main.tsx`:

```tsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```

### 3.8 Final verify

```bash
npm run dev
npm run lint
npm run format:check
```

---

## Full dependency list (for quick reference)

Runtime:
```
react react-dom
react-router-dom
@tanstack/react-query
axios
zustand
react-hook-form
zod
@hookform/resolvers
lucide-react
sonner
date-fns
clsx
tailwind-merge
class-variance-authority
```

Dev:
```
@tailwindcss/vite
@tanstack/react-query-devtools
@types/node
prettier
prettier-plugin-tailwindcss
eslint-config-prettier
tw-animate-css
typescript-eslint
```

---

## Environment matrix

| Env | Frontend | Backend | DB |
|---|---|---|---|
| Local | Vite :5173 | Spring :8080 | H2 |
| Staging | Vercel preview | Render | Aiven (staging) |
| Prod | Vercel/Netlify | Render | Aiven (prod) |

Dev uses Vite proxy `/api` → `http://localhost:8080` (no CORS needed).
Prod uses `VITE_API_BASE_URL` pointing to Render + CORS on backend.

---

## Notes / gotchas

- shadcn v4 style names: `radix-*` = Radix UI primitives; other prefixes = different primitive libs.
- Base color, style, and icon library CANNOT be changed after init without deleting `components.json` + `src/components/ui/` and re-running init.
- `prettier-plugin-tailwindcss` requires Prettier config to list the plugin — handled in `.prettierrc`.
- `cn` import bug in shadcn CLI v4.21: some generated files import from `"cn"` — fix to `"@/lib/utils"`.
- Tailwind v4 uses CSS-first config — no `tailwind.config.ts`. Theme lives in `src/index.css`.
```

---

Save this as `SETUP.md` in your `rentrix-client` root. It's a complete, reusable template — next time you start a React + Vite + shadcn project, just run through it top to bottom.

Ready for **Step 4: Types + Axios client + mock layer + `MOCKS.md`**? 🚀

## Spring Boot 4 / Jackson 3 notes

- Jackson 3 moved from `com.fasterxml.jackson.databind` → `tools.jackson.databind`
- Annotations (`@JsonProperty`, `@JsonIgnore`) are UNCHANGED — still `com.fasterxml.jackson.annotation.*`
- `JavaTimeModule` is no longer needed as a separate module — Java Time support is built into databind
- JJWT 0.12.x still uses Jackson 2 internally — both libraries coexist, no action needed
- When in doubt, search existing code for the correct import