FROM node:14-alpine as build-step
RUN mkdir -p /app
WORKDIR /app
COPY package.json /app
RUN npm install
COPY . /app
ENTRYPOINT ["node_modules/.bin/ng", "serve"]

