package net.mrjosh.homepi.models

import java.math.BigInteger

class Role constructor(
    val id: BigInteger,
    val title: String,
    val administrator: Boolean,
    can_see_accessories: Boolean,
    can_run_accessory: Boolean,
    can_create_accessory: Boolean,
    can_remove_accessory: Boolean,
    can_see_webhook: Boolean,
    can_remove_webhook: Boolean,
    can_create_webhook: Boolean,
    can_see_users: Boolean,
    can_create_user: Boolean,
    can_remove_user: Boolean,
    can_see_roles: Boolean,
    can_create_role: Boolean,
    can_remove_role: Boolean,
    can_see_logs: Boolean
)