package io.github.lukeeey.skin2server;

import cn.nukkit.plugin.PluginBase;
import io.github.lukeeey.skin2server.command.Skin2ServerCommand;
import io.github.lukeeey.skin2server.http.APIService;
import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Getter
public class Skin2ServerPlugin extends PluginBase {
    private APIService apiService;

    @Override
    public void onEnable() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8000")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);

        getServer().getCommandMap().register("skin2server", new Skin2ServerCommand(this));
    }
}
