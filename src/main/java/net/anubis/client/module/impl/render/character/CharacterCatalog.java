package net.anubis.client.module.impl.render.character;

import java.util.List;
import net.anubis.client.module.impl.render.character.designs.Aoi;
import net.anubis.client.module.impl.render.character.designs.Luna;
import net.anubis.client.module.impl.render.character.designs.Mika;
import net.anubis.client.module.impl.render.character.designs.Miku;
import net.anubis.client.module.impl.render.character.designs.Sakura;
import net.anubis.client.module.impl.render.character.designs.Tralalero;
import net.anubis.client.module.impl.render.character.designs.TungTungSahur;
import net.anubis.client.module.impl.render.character.kit.CharacterDesign;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CharacterCatalog {
    private static final List<CharacterDesign> DESIGNS = List.of(new Sakura(), new Luna(), new Mika(), new Aoi(), new Miku(), new TungTungSahur(), new Tralalero());
    private CharacterCatalog() {}
    public static List<CharacterDesign> all() { return DESIGNS; }
    static List<String> names() { return DESIGNS.stream().map(CharacterDesign::name).toList(); }
    static CharacterDesign byName(String name) { return DESIGNS.stream().filter(d -> d.name().equals(name)).findFirst().orElse(DESIGNS.get(0)); }
}
