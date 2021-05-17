package be.isservers.hmb.api;

import be.isservers.hmb.lfg.LFGdataManagement;
import be.isservers.hmb.lfg.library.OrganizedDate;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ApiVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);

    @Override
    public void start() {
        LOGGER.info("Web API Started");

        Router router = Router.router(vertx);
        router.get("/ckadmin/:v").handler(this::isAdmin);
        router.get("/archived/").handler(this::Archived);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(5000);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private void isAdmin(RoutingContext routingContext){
        LOGGER.info("Check isAdmin request");
        String id = routingContext.request().getParam("v");

        final JsonObject jsonObject = new JsonObject();

        Member user = LFGdataManagement.heavenDiscord.getMemberById(id);

        if (user != null) {
            if (user.hasPermission(Permission.MANAGE_SERVER)) {
                jsonObject.put("isAdmin","true");
            }
            else {
                jsonObject.put("isAdmin","false");
            }
        }
        else {
            jsonObject.put("isAdmin","no_found");
        }

        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type","application/json")
                .end(Json.encode(jsonObject));

    }

    private void Archived(RoutingContext routingContext){
        LOGGER.info("Archived request");

        List<OrganizedDateItem> listODI = new ArrayList<>();
        final JsonArray jsonArray = new JsonArray();

        for (OrganizedDate od : LFGdataManagement.listDate) {
            OrganizedDateItem odi = new OrganizedDateItem();
            odi.archived = false;
            odi.od = od;
            listODI.add(odi);
        }
        for (OrganizedDate od : LFGdataManagement.listDateArchived) {
            OrganizedDateItem odi = new OrganizedDateItem();
            odi.archived = true;
            odi.od = od;
            listODI.add(odi);
        }

        for (OrganizedDateItem odi : listODI) {
            OrganizedDate od = odi.od;
            JsonObject jo = od.toJsonObject();
            jo.put("archived",odi.archived);
            jsonArray.add(jo);
        }

        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type","application/json")
                .end(Json.encode(jsonArray));
    }
}

class OrganizedDateItem implements Comparable<OrganizedDateItem>{
    boolean archived;
    OrganizedDate od;

    @Override
    public int compareTo(@NotNull OrganizedDateItem o) {
        return Integer.compare(this.od.getId(), o.od.getId());
    }
}