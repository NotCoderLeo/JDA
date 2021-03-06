/*
 *     Copyright 2015-2016 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.dv8tion.jda.core;

import net.dv8tion.jda.bot.JDABot;
import net.dv8tion.jda.client.JDAClient;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.hooks.IEventManager;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.core.requests.ratelimit.IBucket;
import org.apache.http.HttpHost;

import java.util.List;

public interface JDA
{
    /**
     * The current status of the JDA instance.
     */
    enum Status
    {
        /**JDA is currently setting up supporting systems like the AudioSystem.*/
        INITIALIZING,
        /**JDA has finished setting up supporting systems and is ready to log in.*/
        INITIALIZED,
        /**JDA is currently attempting to log in.*/
        LOGGING_IN,
        /**JDA is currently attempting to connect it's websocket to Discord.*/
        CONNECTING_TO_WEBSOCKET,
        /**JDA has successfully connected it's websocket to Discord and is populating internal objects.
         * This process often takes the longest of all Statuses (besides CONNECTED)*/
        LOADING_SUBSYSTEMS,
        /**JDA has finished loading everything, is receiving information from Discord and is firing events.*/
        CONNECTED,
        /**JDA's main websocket has been disconnected. This <b>DOES NOT</b> mean JDA has shutdown permanently.
         * This is an in-between status. Most likely ATTEMPTING_TO_RECONNECT or SHUTTING_DOWN/SHUTDOWN will soon follow.*/
        DISCONNECTED,
        /**When trying to reconnect to Discord JDA encountered an issue, most likely related to a lack of internet connection,
         * and is waiting to try reconnecting again.*/
        WAITING_TO_RECONNECT,
        /**JDA has been disconnected from Discord and is currently trying to reestablish the connection.*/
        ATTEMPTING_TO_RECONNECT,
        /**JDA has received a shutdown request or has been disconnected from Discord and reconnect is disabled, thus,
         * JDA is in the process of shutting down*/
        SHUTTING_DOWN,
        /**JDA has finished shutting down and this instance can no longer be used to communicate with the Discord servers.*/
        SHUTDOWN,
        /**While attempting to authenticate, Discord reported that the provided authentication information was invalid.*/
        FAILED_TO_LOGIN,
    }

    /**
     * Represents the information used to create this shard.
     */
    class ShardInfo
    {
        int shardId;
        int shardTotal;

        ShardInfo(int shardId, int shardTotal)
        {
            this.shardId = shardId;
            this.shardTotal = shardTotal;
        }

        public int getShardId()
        {
            return shardId;
        }

        public int getShardTotal()
        {
            return shardTotal;
        }
    }

    /**
     * Gets the current {@link net.dv8tion.jda.core.JDA.Status Status} of the JDA instance.
     *
     * @return
     *      Current JDA status.
     */
    Status getStatus();

    /**
     * Changes the internal EventManager.
     * The default EventManager is {@link net.dv8tion.jda.core.hooks.InterfacedEventManager InterfacedEventListener}.
     * There is also an {@link net.dv8tion.jda.core.hooks.AnnotatedEventManager AnnotatedEventManager} available.
     *
     * @param manager
     *          The new EventManager to use
     */
    void setEventManager(IEventManager manager);

    /**
     * Adds all provided listeners to the event-listeners that will be used to handle events.
     * This uses the {@link net.dv8tion.jda.core.hooks.InterfacedEventManager InterfacedEventListener} by default.
     * To switch to the {@link net.dv8tion.jda.core.hooks.AnnotatedEventManager AnnotatedEventManager}, use {@link #setEventManager(IEventManager)}.
     *
     * Note: when using the {@link net.dv8tion.jda.core.hooks.InterfacedEventManager InterfacedEventListener} (default),
     * given listener <b>must</b> be instance of {@link net.dv8tion.jda.core.hooks.EventListener EventListener}!
     *
     * @param listeners
     *          The listener
     */
    void addEventListener(Object... listeners);

    /**
     * Removes all provided listeners from the event-listeners and no longer uses it to handle events.
     *
     * @param listeners
     *          The listener to be removed.
     */
    void removeEventListener(Object... listeners);

    /**
     * Returns an unmodifiable List of Objects that have been registered as EventListeners.
     *
     * @return
     *      List of currently registered Objects acting as EventListeners.
     */
    List<Object> getRegisteredListeners();

    /**
     * An unmodifiable list of all known {@link net.dv8tion.jda.core.entities.User Users}.<br>
     * This list will never contain duplicates and represents all {@link net.dv8tion.jda.core.entities.User Users} that
     * JDA can currently see.
     *
     * @return
     *      List of all known {@link net.dv8tion.jda.core.entities.User Users}.
     */
    List<User> getUsers();

    /**
     * This returns the {@link net.dv8tion.jda.core.entities.User User} who has the same id as the one provided.<br>
     * If there is no known user with an id that matches the provided one, this this returns <code>null</code>.
     *
     * @param id
     *          The id of the {@link net.dv8tion.jda.core.entities.User User}.
     * @return
     *      Possibly-null {@link net.dv8tion.jda.core.entities.User User} with matching id.
     */
    User getUserById(String id);

    /**
     * This unmodifiable returns all {@link net.dv8tion.jda.core.entities.User Users} that have the same username as the one provided.<br>
     * If there are no {@link net.dv8tion.jda.core.entities.User Users} with the provided name, then this returns an empty list.
     *
     * @param name
     *          The name of the requested {@link net.dv8tion.jda.core.entities.User Users}.
     * @param ignoreCase
     *          Whether to ignore case or not.
     * @return
     *      Possibly-empty list of {@link net.dv8tion.jda.core.entities.User Users} that all have the same name as the provided name.
     */
    List<User> getUsersByName(String name, boolean ignoreCase);

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.Guild Guilds} that this account is connected to.<br>
     * If this account is not connected to any {@link net.dv8tion.jda.core.entities.Guild Guilds}, this will return
     * an empty list.
     *
     * @return
     *      Possibly-empty list of all the {@link net.dv8tion.jda.core.entities.Guild Guilds} that this account is connected to.
     */
    List<Guild> getGuilds();

    /**
     * This returns the {@link net.dv8tion.jda.core.entities.Guild Guild} which has the same id as the one provided.<br>
     * If there is no known guild with an id that matches the provided one, then this returns <code>null</code>.
     *
     * @param id
     *          The id of the {@link net.dv8tion.jda.core.entities.Guild Guild}.
     * @return
     *      Possibly-null {@link net.dv8tion.jda.core.entities.Guild Guild} with matching id.
     */
    Guild getGuildById(String id);

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.Guild Guilds} that have the same name as the one provided.<br>
     * If there are no {@link net.dv8tion.jda.core.entities.Guild Guilds} with the provided name, then this returns an empty list.
     *
     * @param name
     *          The name of the requested {@link net.dv8tion.jda.core.entities.Guild Guilds}.
     * @param ignoreCase
     *          Whether to ignore case or not.
     * @return
     *      Possibly-empty list of all the {@link net.dv8tion.jda.core.entities.Guild Guilds} that all have the same name as
     *      the provided name.
     */
    List<Guild> getGuildsByName(String name, boolean ignoreCase);

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.TextChannel TextChannels} of all {@link net.dv8tion.jda.core.entities.Guild Guilds}
     * that this account is a member of.
     * <p>
     * <b>Note:</b> just because a {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} is present in this list does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is possible that you will see fewer channels than this returns. This is because the discord client
     * hides any {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} that you don't have the
     * {@link net.dv8tion.jda.core.Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * @return
     *      Possibly-empty list of all known {@link net.dv8tion.jda.core.entities.TextChannel TextChannels}.
     */
    List<TextChannel> getTextChannels();

    /**
     * This returns the {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} which has the same id as the one provided.<br>
     * If there is no known {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} with an id that matches the provided
     * one, then this returns <code>null</code>.
     * <p>
     * <b>Note:</b> just because a {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} is present does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is you will not see the channel that this returns. This is because the discord client
     * hides any {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} that you don't have the
     * {@link net.dv8tion.jda.core.Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * @param id
     *          The id of the {@link net.dv8tion.jda.core.entities.TextChannel TextChannel}.
     * @return
     *      Possibly-null {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} with matching id.
     */
    TextChannel getTextChannelById(String id);

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.TextChannel TextChannels} that have the same name as the one provided.<br>
     * If there are no {@link net.dv8tion.jda.core.entities.TextChannel TextChannels} with the provided name, then this returns an empty list.
     * <p>
     * <b>Note:</b> just because a {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} is present in this list does
     * not mean that you will be able to send messages to it. Furthermore, if you log into this account on the discord
     * client, it is possible that you will see fewer channels than this returns. This is because the discord client
     * hides any {@link net.dv8tion.jda.core.entities.TextChannel TextChannel} that you don't have the
     * {@link net.dv8tion.jda.core.Permission#MESSAGE_READ Permission.MESSAGE_READ} permission in.
     *
     * @param name
     *          The name of the requested {@link net.dv8tion.jda.core.entities.TextChannel TextChannels}.
     * @param ignoreCase
     *          Whether to ignore case or not.
     * @return
     *      Possibly-empty list of all the {@link net.dv8tion.jda.core.entities.TextChannel TextChannels} that all have the
     *      same name as the provided name.
     */
    List<TextChannel> getTextChannelsByName(String name, boolean ignoreCase);

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannels} of all {@link net.dv8tion.jda.core.entities.Guild Guilds}
     * that this account is a member of.
     *
     * @return
     *      Possible-empty list of all known {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannels}.
     */
    List<VoiceChannel> getVoiceChannels();

    /**
     * This returns the {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannel} which has the same id as the one provided.<br>
     * If there is no known {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannel} with an id that matches the provided
     * one, then this returns <code>null</code>.
     *
     * @param id
     *          The id of the {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannel}.
     * @return
     *      Possibly-null {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannel} with matching id.
     */
    VoiceChannel getVoiceChannelById(String id);

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannels} that have the same name as the one provided.<br>
     * If there are no {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannels} with the provided name, then this returns an empty list.
     *
     * @param name
     *          The name of the requested {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannels}.
     * @param ignoreCase
     *          Whether to ignore case or not.
     * @return
     *      Possibly-empty list of all the {@link net.dv8tion.jda.core.entities.VoiceChannel VoiceChannels} that all have the
     *      same name as the provided name.
     */
    List<VoiceChannel> getVoiceChannelByName(String name, boolean ignoreCase);

    /**
     * An unmodifiable list of all known {@link net.dv8tion.jda.core.entities.PrivateChannel PrivateChannels}.
     *
     * @return
     *      Possibly-empty list of all {@link net.dv8tion.jda.core.entities.PrivateChannel PrivateChannels}.
     */
    List<PrivateChannel> getPrivateChannels();

    /**
     * This returns the {@link net.dv8tion.jda.core.entities.PrivateChannel PrivateChannel} which has the same id as the one provided.<br>
     * If there is no known {@link net.dv8tion.jda.core.entities.PrivateChannel PrivateChannel} with an id that matches the
     * provided one, then this returns <code>null</code>.
     *
     * @param id
     *          The id of the {@link net.dv8tion.jda.core.entities.PrivateChannel PrivateChannel}.
     * @return
     *      Possibly-null {@link net.dv8tion.jda.core.entities.PrivateChannel PrivateChannel} with matching id.
     */
    PrivateChannel getPrivateChannelById(String id);

    /**
     * A collection of all to us known emotes (managed/restricted included)<br>
     * <b>Hint</b>: To check whether you can use an {@link net.dv8tion.jda.core.entities.Emote Emote} you can use
     * {@link net.dv8tion.jda.core.utils.PermissionUtil#canInteract(Member, Emote) PermissionUtil#canInteract(Member, Emote)}.
     *
     * @return
     *      An immutable list of Emotes (which may or may not be available to usage)
     */
    List<Emote> getEmotes();

    /**
     * An unmodifiable list of all {@link net.dv8tion.jda.core.entities.Emote Emotes} that have the same name as the one provided.<br>
     * If there are no {@link net.dv8tion.jda.core.entities.Emote Emotes} with the provided name, then this returns an empty list.
     *
     * @param name
     *          The name of the requested {@link net.dv8tion.jda.core.entities.Emotes Emotes}.
     * @param ignoreCase
     *          Whether to ignore case or not.
     * @return
     *      Possibly-empty list of all the {@link net.dv8tion.jda.core.entities.Emote Emotes} that all have the
     *      same name as the provided name.
     */
    List<Emote> getEmotesByName(String name, boolean ignoreCase);

    /**
     * Retrieves an emote matching the specified <code>id</code> if one is available in our cache.
     *
     * @param id
     *      The emote id to look for
     * @return
     *      An {@link net.dv8tion.jda.core.entities.Emote Emote} represented by this id or null if none is found in our cache
     */
    Emote getEmoteById(String id);

    /**
     * Returns the currently logged in account represented by {@link net.dv8tion.jda.core.entities.SelfUser SelfUser}.<br>
     * Account settings <b>cannot</b> be modified using this object. If you wish to modify account settings please
     *   use the AccountManager.
     *
     * @return
     *      The currently logged in account.
     */
    SelfUser getSelfUser();

    /**
     * The login token that is currently being used for Discord authentication.
     *
     * @return
     *      Never-null, 18 character length string containing the auth token.
     */
    String getToken();

    /**
     * This value is the total amount of JSON responses that discord has sent.<br>
     * This value resets every time the websocket has to reconnect.
     *
     * @return
     *      Never-negative int containing total response amount.
     */
    long getResponseTotal();

    /**
     * The proxy settings used by all JDA instances.
     *
     * @return
     *      The proxy settings used by all JDA instances. If JDA currently isn't using a proxy, {@link java.net.Proxy#NO_PROXY Proxy.NO_PROXY} is returned.
     */
    HttpHost getGlobalProxy();

    /**
     * Sets whether or not JDA should try to reconnect, if a connection-error occured.
     * This will use and incremental reconnect (timeouts are increased each time an attempt fails).
     *
     * Default is true.
     *
     * @param reconnect
     *      If true - enables autoReconnect
     */
    void setAutoReconnect(boolean reconnect);

    /**
     * Returns whether or not autoReconnect is enabled for JDA.
     *
     * @return
     *      True if JDA attempts to autoReconnect
     */
    boolean isAutoReconnect();

    /**
     * Used to determine whether the instance of JDA supports audio and has it enabled.
     *
     * @return
     *      True if JDA can currently utilize the audio system.
     */
    boolean isAudioEnabled();

    /**
     * Used to determine if JDA will process MESSAGE_DELETE_BULK messages received from Discord as a single
     * {@link net.dv8tion.jda.events.message.MessageBulkDeleteEvent MessageBulkDeleteEvent} or split
     * the deleted messages up and fire multiple {@link net.dv8tion.jda.events.message.MessageDeleteEvent MessageDeleteEvents},
     * one for each deleted message.
     * <p>
     * By default, JDA will separate the bulk delete event into individual delete events, but this isn't as efficient as
     * handling a single event would be. It is recommended that BulkDelete Splitting be disabled and that the developer
     * should instead handle the {@link net.dv8tion.jda.events.message.MessageBulkDeleteEvent MessageBulkDeleteEvent}
     *
     * @return
     *      Whether or not JDA currently handles the BULK_MESSAGE_DELETE event by splitting it into individual MessageDeleteEvents or not.
     */
    boolean isBulkDeleteSplittingEnabled();

    /**
     * Shuts down JDA, closing all its connections.
     * After this command is issued the JDA Instance can not be used anymore.
     * This will also close the background-thread used for requests (which is required for further api calls of other JDA instances).
     * If this is not desired, use {@link #shutdown(boolean)} instead.
     * To reconnect, just create a new JDA instance.
     */
    void shutdown();

    /**
     * Shuts down JDA, closing all its connections.
     * After this command is issued the JDA Instance can not be used anymore.
     * Depending on the free-parameter, this will also close the background-thread used for requests.
     * If the background-thread is closed, the system can exit properly, but no further JDA requests are possible (includes other JDA instances).
     * If you want to reconnect, and the request-thread was not freed, just create a new JDA instance.
     *
     * @param free
     *          If true, shuts down JDA's rest system permanently.
     */
    void shutdown(boolean free);

    List<IBucket> shutdownNow(boolean free);

    /**
     * Installs an auxiliary cable into your system.
     *
     * @param port the port
     * @throws UnsupportedOperationException
     */
    void installAuxiliaryCable(int port) throws UnsupportedOperationException;

    AccountType getAccountType();

    JDAClient asClient();

    JDABot asBot();

    ShardInfo getShardInfo();

    /**
     * The {@link net.dv8tion.jda.core.managers.Presence Presence} of the current session
     *
     * @return
     *      The never-null {@link net.dv8tion.jda.core.managers.Presence Presence} of this session
     */
    Presence getPresence();
}
