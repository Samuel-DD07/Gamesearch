## Rodrigue
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./

RUN npm install --legacy-peer-deps && \
    npm install ajv@^8.0.0 ajv-keywords@^5.0.0 react-scripts@^5.0.1 --legacy-peer-deps
COPY . .
RUN npm run build

# Serveur de Production optimisé
FROM nginx:alpine

# Injection de la configuration Nginx (Cache + Routage Client)
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
