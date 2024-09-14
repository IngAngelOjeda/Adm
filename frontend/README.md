## Ultima PrimeNG

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).


// https://pm2.keymetrics.io/docs/usage/application-declaration/
# Start all applications
pm2 start ecosystem.config.js
# Stop all
pm2 stop ecosystem.config.js
# Restart all
pm2 restart ecosystem.config.js
# Reload all
pm2 reload ecosystem.config.js
# Delete all
pm2 delete ecosystem.config.js

pm2 delete all
pm2 start ecosystem.js
pm2 save
pm2 startup

module.exports = {
  apps: [{
    name: "nestjs-prod",
    script: "./dist/main.js", // cluster mode run with node only, not npm
    args: "",
    exec_mode : "cluster", // default fork
    instances: 2, //"max",
    kill_timeout : 4000,
    wait_ready: true,
    autorestart: true,
    watch: false,
    max_memory_restart: "1G",
    log_date_format : "YYYY-MM-DD HH:mm Z",
    env: {
      NODE_ENV: "prod"
    },
  }]
};
