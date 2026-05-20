# ---------- IMAGE OFICIAL DO NODE ----------
# Image do Node para instalar dependências e compilar o Front-end
FROM node:22-alpine AS build
WORKDIR /app

# ---------- DEPENDÊNCIAS E CÓDIGO FONTE ----------
COPY package.json package-lock.json* ./
RUN npm install
COPY . .

# ---------- COMPILAÇÃO DO VITE ----------
# Gera pasta 'dist'
RUN npm run build

# ---------- RODA IMAGE LEVE DE NGINX ----------
# Substitui o Node pelo Nginx para servir o site
FROM nginx:alpine

# ---------- COPIA ARQUIVOS PRONTOS ----------
COPY --from=build /app/dist /usr/share/nginx/html

# Configuração necessária para o React Router não dar erro quando reiniciar página
RUN echo 'server { listen 80; location / { root /usr/share/nginx/html; index index.html; try_files $uri $uri/ /index.html; } }' > /etc/nginx/conf.d/default.conf

# ---------- INICIALIZAÇÃO ----------
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]