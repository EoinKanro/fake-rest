package io.github.eoinkanro.fakerest.ui.handler;

import io.github.eoinkanro.fakerest.core.conf.Config;
import io.github.eoinkanro.fakerest.core.service.HandlerConfigService;
import io.github.eoinkanro.fakerest.ui.model.MainConfigDto;
import io.javalin.http.Context;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Singleton
@RequiredArgsConstructor
public class UpdateConfigApiHandler extends AbstractApiHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HandlerConfigService handlerConfigService;

    @Override
    protected void process(Context context) throws Exception {
        MainConfigDto mainConfigDto = objectMapper.readValue(context.body(), MainConfigDto.class);

        Config config = Config.builder()
            .uiPort(mainConfigDto.getUiPort())
            .mockPort(mainConfigDto.getMockPort())
            .build();

        if (!handlerConfigService.updateMainConfig(config)) {
            throw new ApiHandlerInternalErrorException();
        }
    }

}
