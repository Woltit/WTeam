FROM node:22-alpine AS build

WORKDIR /app

COPY package.json pnpm-lock.yaml* ./
# Use pnpm since it's the package manager used in the project
RUN npm install -g pnpm && pnpm install

COPY . .

RUN pnpm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

# Replace default config to support client-side routing (fallback to index.html)
RUN echo "server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files \$uri \$uri/ /index.html; \
    } \
}" > /etc/nginx/conf.d/default.conf

EXPOSE 80
