# ===================
# === build stage ===
# ===================

# building in node.js version 22, linux alpine image
FROM node:22-alpine AS build

WORKDIR /frontend

COPY package*.json .

RUN npm install

COPY . .

RUN npm run build

# ========================
# === production stage ===
# ========================

# running on nginx, linux alpine image
FROM nginx:alpine AS production

# copy static HTML, CSS, JS/TS files from build stage
COPY --from=build /frontend/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]