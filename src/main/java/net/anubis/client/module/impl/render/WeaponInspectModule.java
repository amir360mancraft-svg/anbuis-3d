package net.anubis.client.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3489;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_811;

@Environment(EnvType.CLIENT)
public final class WeaponInspectModule {
    private static final class_2960 KNIFE_TEXTURE = class_2960.method_60655("anubis3d_standalone", "textures/misc/knife_base.png");
    private static final String[] SKINS = {"Butterfly", "Karambit", "Katana"};
    private static final float[] REST = new float[16];
    private static String selectedSkin = "Karambit";
    private static InspectAnimation.Clip clip;
    private static long startedAt;
    private static boolean inspecting;
    private static boolean enabled = true;

    private WeaponInspectModule() {}

    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("knifeinspect")
                .executes(context -> { startInspect(); return 1; })
                .then(ClientCommandManager.literal("next").executes(context -> { nextSkin(); startInspect(); return 1; }))
                .then(ClientCommandManager.literal("on").executes(context -> { enabled = true; return 1; }))
                .then(ClientCommandManager.literal("off").executes(context -> { enabled = false; inspecting = false; return 1; }))
        ));
    }

    private static void nextSkin() {
        int index = 0;
        for (int i = 0; i < SKINS.length; i++) if (SKINS[i].equals(selectedSkin)) index = i;
        selectedSkin = SKINS[(index + 1) % SKINS.length];
    }

    private static void startInspect() {
        class_746 player = net.minecraft.class_310.method_1551().field_1724;
        if (player == null || !isSword(player.method_6047())) return;
        KnifeSkin knife = KnifeSkin.of(selectedSkin);
        if (knife == null) return;
        clip = knife.inspect;
        startedAt = System.nanoTime();
        inspecting = true;
    }

    private static boolean isSword(class_1799 stack) {
        return !stack.method_7960() && (stack.method_31573(class_3489.field_42611)
            || class_7923.field_41178.method_10221(stack.method_7909()).method_12832().endsWith("_sword"));
    }

    public static void handsBegin(class_4587 pose) {
        // Reserved for future hand-space effects; knife geometry uses the item render pose directly.
    }

    public static boolean renderItem(class_4587 pose, class_1309 holder, class_1799 stack,
                                     class_811 context, class_11659 collector, int light, boolean mainHand) {
        if (!enabled || !mainHand || !isSword(stack)) return false;
        KnifeSkin knife = KnifeSkin.of(selectedSkin);
        if (knife == null) return false;

        float weight = 0.0F;
        float[] state = REST;
        if (inspecting && clip != null) {
            float progress = (System.nanoTime() - startedAt) / 1_000_000_000.0F / clip.seconds;
            if (progress >= 1.0F) {
                inspecting = false;
            } else {
                clip.sample(progress, state = new float[16]);
                weight = 1.0F;
            }
        }

        int side = context == class_811.field_4321 ? -1 : 1;
        KnifeMaterial material = KnifeMaterial.of(stack);
        class_1921 solid = class_12249.method_75994(KNIFE_TEXTURE);
        pose.method_22903();
        if (side < 0) pose.method_22905(-1.0F, 1.0F, 1.0F);
        knife.draw(pose, state, weight, false, 1.0F, new KnifeSkin.Sink() {
            @Override
            public void part(class_4587 part, KnifeMesh mesh, KnifeMesh.Shine shine) {
                collector.method_73483(part, solid, (matrix, vertices) -> mesh.emit(matrix, vertices, material.palette, light, shine));
                if (stack.method_7958()) {
                    collector.method_73529(1).method_73483(part, class_12249.method_75997(),
                        (matrix, vertices) -> mesh.emit(matrix, vertices, material.palette, light, KnifeMesh.Shine.NONE));
                }
            }

            @Override
            public void glow(class_4587 part, KnifeMesh mesh, KnifeMesh.Shine shine) {
                collector.method_73529(1).method_73483(part, class_12249.method_76014(KNIFE_TEXTURE),
                    (matrix, vertices) -> mesh.emitGlow(matrix, vertices, material.glow, shine));
            }

            @Override
            public void arm(class_4587 hand, boolean other) {
                // Arms remain rendered by vanilla; only the knife mesh is replaced.
            }
        }, null);
        pose.method_22909();
        return true;
    }
}
