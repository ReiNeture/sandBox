#-*- coding:utf-8 -*-
import discord
import asyncio

client = discord.Client()

@client.event
async def on_ready():
    #for i in client.servers:
    #    print(i)
    #print(client.user.name)
    #print(client.user.id)
    game = discord.Game('discord.py')
    #await client.change_presence(game=game)

@client.event
async def on_message(message):
    text = message.content

    if text.startswith('$hello'):
        msg = 'Hello {0.author.mention}'.format(message)
        await client.send_message(message.channel, msg)

    if text.startswith('$kiss'):
        if (message.mentions):
            msg = '{0.author.mention} 親了 {0.mentions[0].mention}'.format(message)
            embed = discord.Embed(title='', description=msg, color=0xFFB7DD)
            embed.set_image(url='https://i.imgur.com/JPcH5LK.gif')
            await client.send_message(message.channel, embed=embed)

    if text.startswith('$help'):
        embed = discord.Embed(title="指令集", description='$help - 幫助\n$kiss - 親親', color=0x99FFFF)
        #embed.add_field(name="Author", value="<YOUR-USERNAME>")
		#await message.channel.send(embed=embed)

    if text.startswith('$avatar'):
        if (message.mentions):
            embed = discord.Embed(title='', description='', color=0xFFB7DD)
            embed.set_image(url=message.mentions[0].avatar_url)
            await client.send_message(message.channel, embed=embed)
	
    if text.startswith('$tt'):
        await message.channel.send('fuck?')

    '''if text.startswith('$test'):
        role = None
        for g in message.server.roles:
            if (g.name == 'Test Role'):
                role = g
        
        await client.add_roles( message.mentions[0], role)'''





client.run('NTQwNTg3OTg5MjM2MDU2MDY1.XaGhfQ.3ZBGwfcPdAGiN0LwYtgovC4YdFU')