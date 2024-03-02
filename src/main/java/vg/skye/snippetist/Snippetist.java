package vg.skye.snippetist;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Snippetist implements ClientModInitializer {
	public static final String ID = "snippetist";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	private static final Gson GSON = new Gson();
	public static final Pattern SNIPPET_NAME = Pattern.compile("^[a-z0-9_-]+$");
	public static final Pattern SNIPPET_PARTIAL = Pattern.compile(":[a-z0-9_-]*$");
	public static Map<String, String> snippets = new HashMap<>();

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public void reload(ResourceManager manager) {
				snippets.clear();
				var definitions = manager.findResources("snippetist_snippets", path -> path.getPath().endsWith(".json"));
				for (var entry : definitions.entrySet()) {
					try {
						InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream());
						JsonObject json = JsonHelper.deserialize(GSON, reader, JsonObject.class);
						for (var snippet : json.entrySet()) {
							if (!snippet.getValue().isJsonPrimitive() || !snippet.getValue().getAsJsonPrimitive().isString()) {
								LOGGER.warn("Value for snippet "  + snippet.getKey() + " in " + entry.getKey() + " is not a string!");
								continue;
							}
							if (!SNIPPET_NAME.matcher(snippet.getKey()).matches()) {
								LOGGER.warn("Invalid name for snippet in " + entry.getKey() + ": " + snippet.getKey());
								continue;
							}
							snippets.put(snippet.getKey(), snippet.getValue().getAsString());
						}
					} catch (Exception e) {
						LOGGER.warn("Error loading data in " + entry.getKey());
						e.printStackTrace();
					}
				}
			}

			@Override
			public Identifier getFabricId() {
				return id("snippets_loader");
			}
		});

		ResourceManagerHelper.registerBuiltinResourcePack(
				id("discord"),
				FabricLoader.getInstance().getModContainer(ID).get(),
				Text.of("Discord emoji shortcodes"),
				ResourcePackActivationType.NORMAL
		);
	}

	private static Identifier id(String path) {
		return new Identifier(ID, path);
	}
}