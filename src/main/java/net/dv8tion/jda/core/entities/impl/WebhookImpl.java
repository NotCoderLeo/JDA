package net.dv8tion.jda.core.entities.impl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.*;
import org.apache.http.util.Args;
import org.json.JSONArray;
import org.json.JSONObject;

public class WebhookImpl implements Webhook
{
    private final String id;
    private final GuildImpl guild;
    private final TextChannel channel;
    private final JDA jda;
    
    private String name;
    private String token;
    private String avatar;
    private User author;
    
    public WebhookImpl(String id, Guild guild, TextChannel channel, JDA jda)
    {
        this.id = id;
        this.guild = (GuildImpl) guild;
        this.channel = channel;
        this.jda = jda;
    }
    
    @Override
    public Guild getGuild()
    {
        return guild;
    }
    
    @Override
    public TextChannel getChannel()
    {
        return channel;
    }
    
    @Override
    public User getAuthor()
    {
        return author;
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public String getAvatar()
    {
        return avatar;
    }
    
    @Override
    public String getToken()
    {
        return token;
    }
    
    public WebhookImpl setName(String name)
    {
        Args.notNull(name, "name");
        Args.notBlank(name, "name");
        
        this.name = name;
        
        return this;
    }
    
    public WebhookImpl setToken(String token)
    {
        Args.notNull(token, "token");
        Args.notBlank(token, "token");
        
        this.token = token;
        
        return this;
    }
    
    public WebhookImpl setAuthor(User author)
    {
        if (this.author != null)
            throw new IllegalStateException("Author is already set!");
    
        this.author = author;
        
        return this;
    }
    
    public WebhookImpl setAvatar(String avatar)
    {
        this.avatar = avatar;
        
        return this;
    }
    
    @Override
    public RestAction<Void> execute(MessageEmbed... embeds)
    {
        return execute(null, null, embeds);
    }
    
    @Override
    public RestAction<Void> execute(String username, MessageEmbed... embeds)
    {
        return execute(username, null, embeds);
    }
    
    @Override
    public RestAction<Void> execute(String username, String avatar, MessageEmbed... embeds)
    {
        Args.notNull(embeds, "embeds");
        
        Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK.compile(getId(), getToken());
        JSONObject jsonData = new JSONObject();
        JSONArray embedsArray = new JSONArray();
        
        for (MessageEmbed embed : embeds)
        {
            embedsArray.put(((MessageEmbedImpl) embed).toJSONObject());
        }
        
        jsonData.put("tts", false);
        jsonData.put("embeds", embedsArray);
        jsonData.put("username", username);
        jsonData.put("avatar_url", avatar);
        
        return new RestAction<Void>(getJDA(), route, jsonData)
        {
            @Override
            protected void handleResponse(Response response, Request request)
            {
                if (response.isOk())
                    request.onSuccess(null);
                else
                    request.onFailure(response);
            }
        };
    }
    
    @Override
    public RestAction<Webhook> editWebhook(Webhook newWebhook)
    {
        Args.notNull(newWebhook, "newWebhook");
        
        checkPermission(Permission.MANAGE_WEBHOOKS);
        
        JSONObject jsonData = new JSONObject()
                .put("name", newWebhook.getName())
                .put("avatar", newWebhook.getAvatar());
        Route.CompiledRoute route = Route.Webhooks.MODIFY_WEBHOOK.compile(getId());
        
        return new RestAction<Webhook>(getJDA(), route, jsonData)
        {
            @Override
            protected void handleResponse(Response response, Request request)
            {
                if (response.isOk())
                {
                    try
                    {
                        JSONObject data = response.getObject();
                        
                        request.onSuccess(EntityBuilder.get(getJDA())
                                .createWebhook(data, data.has("guild_id") && !data.isNull("guild_id") ? data.getString("guild_id") : null));
                    } catch (IllegalArgumentException e)
                    {
                        request.onFailure(e);
                    }
                } else
                {
                    request.onFailure(response);
                }
            }
        };
    }
    
    @Override
    public RestAction<Void> deleteWebhook()
    {
        checkPermission(Permission.MANAGE_WEBHOOKS);
        
        Route.CompiledRoute route = Route.Webhooks.DELETE_WEBHOOK.compile(getId());
        
        return new RestAction<Void>(getJDA(), route, null)
        {
            @Override
            protected void handleResponse(Response response, Request request)
            {
                if (response.isOk())
                {
                    request.onSuccess(null);
                    
                    api.getWebhookMap().remove(getId());
                } else
                {
                    ErrorResponse error = ErrorResponse.fromJSON(response.getObject());
                    if (error == ErrorResponse.MISSING_PERMISSIONS)
                    {
                        // Make sure we still have permission to manage webhooks.
                        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_WEBHOOKS))
                            request.onFailure(new PermissionException(Permission.MANAGE_WEBHOOKS));
                        else
                            request.onFailure(new PermissionException(Permission.MANAGE_WEBHOOKS,
                                    "You need MANAGE_WEBHOOKS permission to delete webhooks"));
                    } else
                    {
                        request.onFailure(response);
                    }
                }
            }
        };
    }
    
    @Override
    public Webhook copy()
    {
        return new WebhookImpl(id, guild, channel, jda);
    }
    
    @Override
    public String getId()
    {
        return id;
    }
    
    public JDA getJDA()
    {
        return jda;
    }
    
    private void checkPermission(Permission permission)
    {
        if (guild == null)
        {
            throw new IllegalStateException("No guild is set!");
        }
        
        if (!guild.getSelfMember().hasPermission(permission))
            throw new PermissionException(permission);
    }
}
