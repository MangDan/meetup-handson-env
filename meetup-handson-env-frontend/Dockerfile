FROM node:latest

RUN npm install -g http-server

# /app 디렉토리 생성
RUN mkdir -p /usr/src/app
# /app 디렉토리를 WORKDIR 로 설정
WORKDIR /usr/src/app
# 현재 Dockerfile 있는 경로의 모든 파일을 /app 에 복사
COPY . /usr/src/app
# npm install 을 실행
RUN npm install

RUN npm run build

#가상 머신에 오픈할 포트
EXPOSE 8080

#컨테이너에서 실행될 명령을 지정
CMD [ "http-server", "dist" ]



# # install simple http server for serving static content
# RUN npm install -g http-server

# # make the 'app' folder the current working directory
# WORKDIR /app

# # copy both 'package.json' and 'package-lock.json' (if available)
# COPY package*.json ./

# # install project dependencies
# RUN npm install --production

# # copy project files and folders to the current working directory (i.e. 'app' folder)
# COPY . .

# # build app for production with minification
# RUN npm run build

# # Test Expose 
# EXPOSE 8080

# CMD [ "http-server", "dist" ]