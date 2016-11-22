package net.dv8tion.jda.core.entities;

import net.dv8tion.jda.core.requests.RestAction;

/**
 * Represents a Discord {@link net.dv8tion.jda.core.entities.Webhook Webhook}. This should contain all information provided from Discord about a Webhook.
 */
public interface Webhook extends ISnowflake
{
    /**
     * Returns the {@link net.dv8tion.jda.core.entities.Guild Guild} that this Webhook is for.
     *
     * @return
     *      Sometimes-null {@link net.dv8tion.jda.core.entities.Guild Guild} that this Webhook is for.
     */
    Guild getGuild();
    
    /**
     * Returns the {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} that this Webhook is for.
     *
     * @return
     *      Never-null {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} that this Webhook is for.
     */
    TextChannel getChannel();
    
    /**
     * Returns the {@link net.dv8tion.jda.core.entities.User User} that created this Webhook.
     * Isn't returned when retrieving by token.
     *
     * @return
     *      Sometimes-null {@link net.dv8tion.jda.core.entities.User User} that created this Webhook.
     */
    User getAuthor();
    
    /**
     * The name of this webhook.
     *
     * @return
     *      Sometimes-null {@link java.lang.String String} that contains the name of this Webhook
     */
    String getName();
    
    /**
     * The avatar of this webhook.
     *
     * @return
     *      Sometimes-null {@link java.lang.String String} that contains the URL to the Webhook's avatar
     */
    String getAvatar();
    
    /**
     * The token of this webhook.
     *
     * @return
     *      Never-null {@link java.lang.String String} that contains the webhook's token.
     */
    String getToken();
    
    /**
     * Execute this webhook.
     *
     * This Object will be null, if the sending failed.
     *
     * @param embeds
     *          the embeds to send
     * @return
     *      Nothing
     * @throws net.dv8tion.jda.core.exceptions.RateLimitedException
     *      when rate-imit is reached
     * @throws net.dv8tion.jda.core.exceptions.PermissionException
     *      If this is a {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} and the logged in account does
     *      not have {@link net.dv8tion.jda.core.Permission#MESSAGE_WRITE Permission.MESSAGE_WRITE}.
     */
    RestAction<Void> execute(MessageEmbed... embeds);
    
    /**
     * Execute this webhook with a custom username.
     *
     * This Object will be null, if the sending failed.
     *
     * @param embeds
     *          the embeds to send
     * @param username
     *          a custom username (can be null)
     * @return
     *      Nothing
     * @throws net.dv8tion.jda.core.exceptions.RateLimitedException
     *      when rate-imit is reached
     * @throws net.dv8tion.jda.core.exceptions.PermissionException
     *      If this is a {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} and the logged in account does
     *      not have {@link net.dv8tion.jda.core.Permission#MESSAGE_WRITE Permission.MESSAGE_WRITE}.
     */
    RestAction<Void> execute(String username, MessageEmbed... embeds);
    
    /**
     * Execute this webhook with a custom username and avatar.
     *
     * This Object will be null, if the sending failed.
     *
     * @param embeds
     *          the embeds to send
     * @param username
     *          a custom username (can be null)
     * @param avatar
     *          the URL to a custom avatar (can be null)
     * @return
     *      Nothing
     * @throws net.dv8tion.jda.core.exceptions.RateLimitedException
     *      when rate-imit is reached
     * @throws net.dv8tion.jda.core.exceptions.PermissionException
     *      If this is a {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} and the logged in account does
     *      not have {@link net.dv8tion.jda.core.Permission#MESSAGE_WRITE Permission.MESSAGE_WRITE}.
     */
    RestAction<Void> execute(String username, String avatar, MessageEmbed... embeds);
    
    /**
     * Edits this Webhooks info to be the info of the provided webhook.
     * If this method failed, null gets returned
     *
     * @param newWebhook the new info of the Webhook
     * @return a new Webhook object for the edited webhook
     */
    RestAction<Webhook> editWebhook(Webhook newWebhook);
    
    /**
     * Deletes this Webhook from the server.
     *
     * @return
     *          {@link net.dv8tion.jda.core.requests.RestAction RestAction}
     */
    RestAction<Void> deleteWebhook();
    
    /**
     * Create a copy of this Webhook that can be modified.
     * NOTE: This does not create a copy on Discord's servers!
     *
     * @return
     *          {@link net.dv8tion.jda.core.entities.Webhook Webhook}
     */
    Webhook copy();
}
