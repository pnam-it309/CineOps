# Stage 1: Development / Dependency stage
FROM node:20-alpine AS build-stage
WORKDIR /app

# Install dependencies first for better Docker layer caching
COPY package*.json ./
RUN npm install

# Copy source code (Needed for production build, but volumes will override in dev)
COPY . .

# Stage 2: Production build stage
FROM build-stage AS build-production
RUN npm run build 

# Stage 3: Production stage
FROM nginx:stable-alpine AS production-stage
WORKDIR /app
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build-production /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
