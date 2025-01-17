package bisq.web.server.handler;

import bisq.web.json.JsonTransform;
import ratpack.error.ClientErrorHandler;
import ratpack.handling.Context;

import java.util.Map;

public class BisqClientErrorHandler extends AbstractHandler implements ClientErrorHandler {

    public BisqClientErrorHandler(JsonTransform jsonTransform) {
        super(jsonTransform);
    }

    @Override
    public void error(Context context, int statusCode) {
        String whatNotFound = context.getRequest().getPath();
        Map<String, Object> error = toMap("error", Integer.toString(statusCode));
        error.put("proto", whatNotFound + " not found");
        context.getResponse().status(statusCode).send(toJson(error));
    }

    @Override
    public void handle(Context ctx) {
        // not implemented
    }
}
