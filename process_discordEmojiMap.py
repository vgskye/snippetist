import requests
import json

result = {}
response = requests.get("https://emzi0767.gl-pages.emzi0767.dev/discord-emoji/discordEmojiMap.min.json").json()
for entry in response["emojiDefinitions"]:
    for name in entry["names"]:
        if name in entry["namesWithColons"]:
            continue
        result[name] = entry["surrogates"]

with open("src/main/resources/resourcepacks/discord/assets/discord/snippetist_snippets/discord.json", "w") as f:
    json.dump(result, f, separators=(',', ':'))
print(f"found {len(result)} snippets")
