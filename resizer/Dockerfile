FROM visla/node-imagemagick:6

# We expose port 80, default where all micro-services run on.
EXPOSE 80
WORKDIR /app

COPY . /app/
WORKDIR /app
RUN npm install

# Run the app with corrent envvars.
CMD ["node", "index"]
