package net.anubis.client.module.impl.render.character;

import net.anubis.client.module.impl.render.character.kit.CharacterDesign;
import net.anubis.client.module.impl.render.character.kit.CharacterModel;
import net.anubis.client.module.impl.render.character.kit.Detail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.class_10017;
import net.minecraft.class_10055;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3887;
import net.minecraft.class_4507;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_970;
import net.minecraft.class_972;
import net.minecraft.class_976;
import net.minecraft.class_978;
import net.minecraft.class_979;
import net.minecraft.class_983;
import net.minecraft.class_989;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public final class CharacterModule {
    private static final Logger LOGGER = LoggerFactory.getLogger("Anubis3D");
    private static boolean enabled = true;
    private static int selected;
    private static Detail detail = Detail.MEDIUM;
    private static class_638 lastLevel;

    private CharacterModule() {}

    public static void initialize() {
        CharacterCache.prepare(currentDesign(), detail);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("character3d")
                .executes(ctx -> { toggle(); return 1; })
                .then(ClientCommandManager.literal("next").executes(ctx -> { next(); return 1; }))
                .then(ClientCommandManager.literal("on").executes(ctx -> { enabled = true; prepare(); return 1; }))
                .then(ClientCommandManager.literal("off").executes(ctx -> { enabled = false; CharacterCache.clear(); return 1; }))
        ));
    }

    private static CharacterDesign currentDesign() { return CharacterCatalog.all().get(selected % CharacterCatalog.all().size()); }
    private static void prepare() { if (enabled) CharacterCache.prepare(currentDesign(), detail); }
    private static void toggle() { enabled = !enabled; if (enabled) prepare(); else CharacterCache.clear(); }
    private static void next() { selected = (selected + 1) % CharacterCatalog.all().size(); prepare(); }

    public static void tick() {
        class_310 mc = class_310.method_1551();
        if (lastLevel != mc.field_1687) {
            lastLevel = mc.field_1687;
            CharacterCache.releaseTextures();
            prepare();
        }
    }

    public static void extract(class_1297 entity, class_10055 state) {
        CharacterHolder holder = (CharacterHolder) state;
        WornCharacter worn = resolve(state);
        holder.anubis$setCharacter(worn);
        if (worn != null) {
            class_1297 vehicle = entity.method_5854();
            holder.anubis$setSeat(vehicle == null ? 0.0F : (vehicle instanceof class_1309 ? 1.35F : 0.3F));
            if (state.field_53338 != null) {
                CharacterModel model = worn.model;
                float over = model.maxY * model.scale - 32.0F;
                if (over > 0.0F) {
                    state.field_53338 = state.field_53338.method_1031(0.0, over * 0.05859375F * state.field_53453, 0.0);
                }
            }
        } else {
            holder.anubis$setSeat(0.0F);
        }
    }

    private static WornCharacter resolve(class_10055 state) {
        if (!enabled || state.field_53542) return null;
        class_310 mc = class_310.method_1551();
        class_746 self = mc.field_1724;
        if (self == null || mc.field_1687 == null || state.field_53528 != self.method_5628()) return null;
        return CharacterCache.ready(currentDesign(), detail);
    }

    static WornCharacter worn(class_10017 state) {
        return state instanceof CharacterHolder holder ? holder.anubis$character() : null;
    }

    static float seat(class_10017 state) {
        return state instanceof CharacterHolder holder ? holder.anubis$seat() : 0.0F;
    }

    public static boolean hidesModel(class_10017 state) { return worn(state) != null; }

    public static boolean hidesLayer(class_3887<?, ?> layer, class_10017 state) {
        if (worn(state) == null) return false;
        return layer instanceof class_970 || layer instanceof class_972 || layer instanceof class_979
            || layer instanceof class_4507 || layer instanceof class_976 || layer instanceof class_983 || layer instanceof class_978 || layer instanceof class_989;
    }

    static WornCharacter firstPerson() {
        if (!enabled) return null;
        class_746 self = class_310.method_1551().field_1724;
        if (self == null || self.method_7325()) return null;
        WornCharacter worn = CharacterCache.ready(currentDesign(), detail);
        return worn != null && worn.model.firstPersonArms ? worn : null;
    }

    static void failed(CharacterDesign design, Throwable failure) {
        LOGGER.error("Failed to build 3D character {}", design.name(), failure);
    }

    public static boolean isEnabled() { return enabled; }
}
