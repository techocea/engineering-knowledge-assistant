# --- Stage 1: Build the React application with pnpm ---
FROM node:20-alpine AS builder
WORKDIR /app

# Enable pnpm via Corepack
RUN corepack enable && corepack prepare pnpm@latest --activate

# Copy package dependency manifests and pnpm lockfile
COPY package.json pnpm-lock.yaml ./

# Install dependencies using pnpm fetch / install
RUN pnpm install --frozen-lockfile

# Copy source code and build
COPY . .
RUN pnpm run build

# --- Stage 2: Serve with Nginx ---
FROM nginx:alpine
# Copy built static files from builder stage to Nginx web root
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 5173 (matching your compose file)
EXPOSE 5173

# Configure nginx to listen on port 5173
RUN sed -i 's/listen       80;/listen       5173;/g' /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]