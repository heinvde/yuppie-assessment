FROM clojure:lein

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app

CMD ["lein", "ring", "server-headless"]
