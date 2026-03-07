# Stage 1: Build stage
FROM node:18-alpine AS build-stage
WORKDIR /app

# Install dependencies first for better Docker layer caching
COPY package*.json ./
RUN npm install

# Build application
COPY . .
RUN npm run build 

# Stage 2: Production stage
FROM nginx:stable-alpine AS production-stage
WORKDIR /app

# Install non-root user (optional but good practice for Nginx)
# Usually Nginx runs under 'nginx' already. We'll stick to that.

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy build artifacts from build stage
# Vite outputs to /app/dist 
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Expose port and start
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
