package be.isservers.hmb.web;

import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.lfg.library.OrganizedDate;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.templ.pebble.PebbleTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainVerticle extends AbstractVerticle{
    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
    private static SessionStore sessionStore;
    private static OAuth2Auth oauth2;

    public MainVerticle(Vertx vertx) {
        sessionStore = LocalSessionStore.create(vertx);
        oauth2 = OAuth2Auth.create(vertx, getOAuth2Options());
    }

    private static OAuth2Options getOAuth2Options() {
        return new OAuth2Options()
                .setFlow(OAuth2FlowType.AUTH_CODE)
                .setClientId("550692924715958283")
                .setClientSecret("izv01cY1JlDGORdkcV4NwVEgl9xWqc6o")
                .setSite("https://discordapp.com/api/oauth2")
                .setTokenPath("/token")
                .setAuthorizationPath("/authorize")
                .setRevocationPath("/token/revoke")
                .setHeaders(new JsonObject().put("Content-Type", "application/x-www-form-urlencoded"));
    }

    @Override
    public void start() throws Exception {
        super.start();

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(SessionHandler.create(sessionStore));
        router.route("/").handler(this::index);
        router.route("/index").handler(this::index);
        router.route("/logs").handler(this::logs);
        router.route("/command/:cat").handler(this::command);
        router.route("/archive").handler(this::archive);

        router.route("/discord").handler(this::discord);
        router.route("/callback").handler(this::discordCallback);
        router.route("/logout").handler(this::logout);

        router.route("/css/*").handler(StaticHandler.create("css/"));
        router.route("/js/*").handler(StaticHandler.create("js/"));
        router.route("/images/*").handler(StaticHandler.create("images/"));

        router.errorHandler(404, this::error_404);

        server.requestHandler(router).listen(8080);
        LOGGER.info("Serveur web demarre");
    }

    private void error_404(RoutingContext routingContext) {
        routingContext.session().put("state", new SweetAlert2(SweetAlert2.WARNING, "Erreur 404: Page non trouve"));
        routingContext.reroute("/");
    }

    private void index(RoutingContext routingContext) {
        final TemplateEngine engine = PebbleTemplateEngine.create(vertx);

        Map<String, Object> vars = new HashMap<>();
        vars.put("home", "home");

        if (routingContext.session().get("state") != null) {
            vars.put("sweetalert", ((SweetAlert2) routingContext.session().get("state")).generate());
            routingContext.session().remove("state");
        }
        if (routingContext.session().get("data") != null) {
            SessionData sessionData = routingContext.session().get("data");
            vars.put("image", sessionData.getImageLink());
            vars.put("name", sessionData.getName());
        }

        Locale.setDefault(Locale.FRENCH);
        engine.render(vars, "pages/index", res -> {
            if (res.succeeded()) {
                routingContext.response().end(res.result());
            } else {
                routingContext.fail(res.cause());
            }
        });
    }

    private void discord(RoutingContext routingContext) {
        String authorization_uri = oauth2.authorizeURL(new JsonObject()
                .put("redirect_uri", "http://localhost:8080/callback")
                .put("scope", "identify")
                .put("response_type", "code"));

        routingContext.response().putHeader("Location", authorization_uri)
                .setStatusCode(302)
                .end();
    }

    private void discordCallback(RoutingContext routingContext) {
        JsonObject tokenConfig = new JsonObject()
                .put("code", routingContext.request().getParam("code"))
                .put("redirectUri", "http://localhost:8080/callback");

        oauth2.authenticate(tokenConfig)
                .onSuccess(user -> WebClient.create(vertx)
                        .getAbs("https://discordapp.com/api/users/@me")
                        .bearerTokenAuthentication(user.principal().getString("access_token"))
                        .send()
                        .onSuccess(response -> {
                            JsonObject jsonObject = response.bodyAsJsonObject();
                            routingContext.session().put(
                                    "data",
                                    new SessionData(
                                            jsonObject.getString("id"),
                                            jsonObject.getString("username"),
                                            jsonObject.getString("avatar"))
                            );
                            routingContext.session().put("state", new SweetAlert2(SweetAlert2.SUCCESS, "Connexion reussie"));
                            routingContext.response().putHeader("Location", "/index")
                                    .setStatusCode(302)
                                    .end();

                        })
                        .onFailure(err -> {
                            routingContext.session().put("state", new SweetAlert2(SweetAlert2.ERROR, "Impossible d'obtenir vos informations depuis les serveurs Discord"));
                            routingContext.response().putHeader("Location", "/index")
                                    .setStatusCode(302)
                                    .end();
                        })
                        .onComplete(event -> oauth2.revoke(user)))
                .onFailure(err -> {
                    routingContext.session().put("state", new SweetAlert2(SweetAlert2.ERROR, "Erreur de connexion aux serveurs Discord"));
                    routingContext.response().putHeader("Location", "/index")
                            .setStatusCode(302)
                            .end();
                });
    }

    private void logs(RoutingContext routingContext) {
        final TemplateEngine engine = PebbleTemplateEngine.create(vertx);

        Map<String, Object> vars = new HashMap<>();
        vars.put("logs", "logs");

        if (routingContext.session().get("data") != null) {
            SessionData sessionData = routingContext.session().get("data");
            vars.put("image", sessionData.getImageLink());
            vars.put("name", sessionData.getName());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);

        WebClient.create(vertx)
                .getAbs("https://www.warcraftlogs.com:443/v1/reports/guild/H%C3%ABaven/elune/EU?start=" + calendar.getTimeInMillis() + "&api_key=ac847c8b762b99a3acef40e31eb6836a")
                .send()
                .onSuccess(response -> {
                    JsonArray jsonArray = response.bodyAsJsonArray();
                    StringBuilder sb = new StringBuilder();
                    for (Object o : jsonArray) {
                        JsonObject entries = (JsonObject) o;
                        sb.append(
                                new RenderLogs(
                                        entries.getString("id"),
                                        entries.getString("title"),
                                        entries.getString("owner"),
                                        entries.getString("start"))
                                        .render()
                        ).append("<hr>");
                    }
                    vars.put("data", sb.substring(0, sb.length() - 4));
                })
                .onComplete(event -> engine.render(vars, "pages/logs", res -> {
                    if (res.succeeded()) {
                        routingContext.response().end(res.result());
                    } else {
                        routingContext.fail(res.cause());
                    }
                }));
    }

    private void logout(RoutingContext routingContext) {
        routingContext.session().remove("data");
        routingContext.session().put("state", new SweetAlert2(SweetAlert2.SUCCESS, "Deconnexion reussie"));
        routingContext.response().putHeader("Location", "/")
                .setStatusCode(302)
                .end();
    }

    private void command(RoutingContext routingContext) {
        final TemplateEngine engine = PebbleTemplateEngine.create(vertx);

        String category = routingContext.request().getParam("cat");

        Map<String, Object> vars = new HashMap<>();
        vars.put(category,category);
        vars.put("category",category);

        if (routingContext.session().get("data") != null) {
            SessionData sessionData = routingContext.session().get("data");
            vars.put("image", sessionData.getImageLink());
            vars.put("name", sessionData.getName());
        }

        try {
            File file = new File(getClass().getClassLoader().getResource("data/"+category+".json").getFile());
            JsonArray jsonArray = new JsonArray(new String(Files.readAllBytes(file.toPath())));

            JsonObject infoCategory = jsonArray.getJsonObject(0);
            vars.put("nameCategory",infoCategory.getValue("titlePage"));
            vars.put("descriptionCategory",infoCategory.getValue("descriptionPage"));
            jsonArray.remove(0);

            StringBuilder sb = new StringBuilder();
            for (Object o : jsonArray) {
                JsonObject object = (JsonObject) o;
                RenderCommands command = new RenderCommands(object);
                sb.append(command.printCommand());
            }
            vars.put("commands",sb.toString());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        engine.render(vars, "pages/command", res -> {
            if (res.succeeded()) {
                routingContext.response().end(res.result());
            } else {
                routingContext.fail(res.cause());
            }
        });
    }

    public void archive(RoutingContext routingContext) {
        final TemplateEngine engine = PebbleTemplateEngine.create(vertx);

        Map<String, Object> vars = new HashMap<>();
        vars.put("archive","archive");

        if (routingContext.session().get("data") != null) {
            SessionData sessionData = routingContext.session().get("data");
            vars.put("image", sessionData.getImageLink());
            vars.put("name", sessionData.getName());
        }

        StringBuilder sb = new StringBuilder();
        for (OrganizedDate organizedDate : LFGdataManagement.listDate) {
            sb.append(new RenderEvents(organizedDate,false).build());
        }
        for (OrganizedDate organizedDate : LFGdataManagement.listDateArchived) {
            sb.append(new RenderEvents(organizedDate,true).build());
        }
        vars.put("events",sb.toString());

        engine.render(vars, "pages/archive", res -> {
            if (res.succeeded()) {
                routingContext.response().end(res.result());
            } else {
                routingContext.fail(res.cause());
            }
        });


    }
}
