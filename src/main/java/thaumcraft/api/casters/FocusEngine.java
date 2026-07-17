package thaumcraft.api.casters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class FocusEngine {

    public static HashMap<String, Class<? extends IFocusElement>> elements = new HashMap<>();
    private static final HashMap<String, ResourceLocation> elementIcons = new HashMap<>();
    private static final HashMap<String, Integer> elementColor = new HashMap<>();

    public static void registerElement(Class<? extends IFocusElement> element, ResourceLocation icon, int color) {
        try {
            IFocusElement fe = element.getDeclaredConstructor().newInstance();
            elements.put(fe.getKey(), element);
            elementIcons.put(fe.getKey(), icon);
            elementColor.put(fe.getKey(), color);
        } catch (Exception ignored) {}
    }

    public static IFocusElement getElement(String key) {
        try {
            return elements.get(key).getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {}
        return null;
    }

    public static ResourceLocation getElementIcon(String key) {
        return elementIcons.get(key);
    }

    public static int getElementColor(String key) {
        return elementColor.getOrDefault(key, 0xFFFFFF);
    }

    public static boolean doesPackageContainElement(FocusPackage focusPackage, String key) {
        for (IFocusElement node : focusPackage.nodes) {
            if (node.getKey().equals(key)) return true;
        }
        return false;
    }

    /**
     * @param nocopy set to true only if the focus package passed in is temporary and not attached to an actual focus.
     * Use this to preserve any settings, targets, etc that has been set during package construction
     */
    public static void castFocusPackage(LivingEntity caster, FocusPackage focusPackage, boolean nocopy) {
        FocusPackage focusPackageCopy;
        if (nocopy)
            focusPackageCopy = focusPackage;
        else
            focusPackageCopy = focusPackage.copy(caster);

        focusPackageCopy.initialize(caster);
        focusPackageCopy.setUniqueID(UUID.randomUUID());
        for (FocusEffect effect : focusPackageCopy.getFocusEffects()) {
            effect.onCast(caster);
        }

        runFocusPackage(focusPackageCopy, null, null);
    }

    /**
     * Overrides castFocusPackage(LivingEntity caster, FocusPackage focusPackage, boolean nocopy) with nocopy = false
     */
    public static void castFocusPackage(LivingEntity caster, FocusPackage focusPackage) {
        castFocusPackage(caster, focusPackage, false);
    }

    public static void runFocusPackage(FocusPackage focusPackage, Trajectory[] trajectories, HitResult[] targets) {

        Trajectory[] prevTrajectories = trajectories;
        HitResult[] prevTargets = targets;

        synchronized (focusPackage.nodes) {

            if (!(focusPackage.nodes.get(0) instanceof FocusMediumRoot)) {
                focusPackage.nodes.add(0, new FocusMediumRoot(trajectories, targets));
            }

            for (int idx = 0; idx < focusPackage.nodes.size(); idx++) {

                focusPackage.setExecutionIndex(idx);

                IFocusElement node = focusPackage.nodes.get(idx);
                if (idx > 0 && node instanceof FocusNode fnode && fnode.getParent() == null) {
                    IFocusElement nodePrev = focusPackage.nodes.get(idx - 1);
                    if (nodePrev instanceof FocusNode prevNode) {
                        fnode.setParent(prevNode);
                    }
                }

                if (node instanceof FocusNode fnode && fnode.getPackage() == null) {
                    fnode.setPackage(focusPackage);
                }

                if (node instanceof FocusNode fnode) {
                    focusPackage.multiplyPower(fnode.getPowerMultiplier());
                }

                if (node instanceof FocusPackage pack) {
                    runFocusPackage(pack, prevTrajectories, prevTargets);
                    break;
                } else if (node instanceof FocusMedium medium) {
                    if (prevTrajectories != null)
                        for (Trajectory trajectory : prevTrajectories) {
                            medium.execute(trajectory);
                        }

                    if (medium.hasIntermediary()) break;
                } else if (node instanceof FocusMod mod) {
                    if (node instanceof FocusModSplit split) {
                        for (FocusPackage sp : split.getSplitPackages()) {
                            split.setPackage(sp);
                            sp.multiplyPower(focusPackage.getPower());
                            split.execute();
                            runFocusPackage(sp, split.supplyTrajectories(), split.supplyTargets());
                        }
                        break;
                    } else {
                        mod.execute();
                    }
                } else if (node instanceof FocusEffect effect) {
                    if (prevTargets != null) {
                        int num = 0;
                        for (HitResult target : prevTargets) {
                            Entity hitEntity = target instanceof EntityHitResult ehr ? ehr.getEntity() : null;
                            if (hitEntity != null) {
                                String k = hitEntity.getId() + focusPackage.getUniqueID().toString();
                                if (damageResistList.contains(k) && hitEntity.invulnerableTime > 0) {
                                    hitEntity.invulnerableTime = 0;
                                } else {
                                    if (damageResistList.size() > 10) damageResistList.remove(0);
                                    damageResistList.add(k);
                                }
                            }
                            Trajectory tra = prevTrajectories != null ? ((prevTrajectories.length == prevTargets.length) ? prevTrajectories[num] : prevTrajectories[0]) : null;
                            effect.execute(target, tra, focusPackage.getPower(), num);
                            num++;
                        }
                    }
                }

                if (node instanceof FocusNode fnode) {
                    prevTrajectories = fnode.supplyTrajectories();
                    prevTargets = fnode.supplyTargets();
                }

            }
        }

    }

    private static final ArrayList<String> damageResistList = new ArrayList<>();

}
