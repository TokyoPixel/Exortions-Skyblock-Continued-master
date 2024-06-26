package com.skyblock.skyblock.utilities.command.annotations;

import com.skyblock.skyblock.features.ranks.PlayerRank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RankPermission {

    PlayerRank permission();
}
