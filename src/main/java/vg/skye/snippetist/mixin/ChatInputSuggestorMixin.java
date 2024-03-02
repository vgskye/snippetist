package vg.skye.snippetist.mixin;

import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vg.skye.snippetist.SnippetSuggestion;
import vg.skye.snippetist.Snippetist;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {
    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow public abstract void show(boolean narrateFirstSuggestion);

    @Inject(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandSource;suggestMatching(Ljava/lang/Iterable;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER))
    private void inject(CallbackInfo ci) {
        String text = this.textField.getText();
        int cursor = this.textField.getCursor();
        String textUptoCursor = text.substring(0, cursor);
        var matcher = Snippetist.SNIPPET_PARTIAL.matcher(textUptoCursor);
        if (matcher.find()) {
            this.pendingSuggestions = CompletableFuture.completedFuture(SnippetSuggestion.suggest(textUptoCursor, matcher.start()));
            this.show(false);
        }
    }
}