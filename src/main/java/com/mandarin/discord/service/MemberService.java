package com.mandarin.discord.service;

import com.mandarin.discord.enums.GuildRole;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class MemberService {

    public static boolean isMissingAppropriateRole(Member member, List<GuildRole> appropriateRoles) {

        return member
                .getRoles()
                .stream()
                .map(Role::getId)
                .noneMatch(rid -> appropriateRoles.stream().map(GuildRole::getRoleId).toList().contains(rid));
    }
}
