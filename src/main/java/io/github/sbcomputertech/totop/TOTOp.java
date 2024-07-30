// Copyright 2024 reddust9
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.github.sbcomputertech.totop;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class TOTOp extends JavaPlugin
{
    private final String _secretKey;
    private final CodeVerifier _verifier;

    public TOTOp()
    {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        _secretKey = secretGenerator.generate();

        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA256, 6);
        TimeProvider timeProvider = new SystemTimeProvider();
        _verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Objects.requireNonNull(getCommand("totop")).setExecutor(this);
        Objects.requireNonNull(getCommand("deopme")).setExecutor(this);
        getLogger().info("Registered commands");

        getLogger().info("Generating QR code...");
        QrData qr = new QrData.Builder()
                .secret(_secretKey)
                .issuer("TOTOp")
                .label(getServer().getMotd())
                .algorithm(HashingAlgorithm.SHA256)
                .digits(6)
                .period(30)
                .build();

        QrGenerator qrGen = new AsciiArtQrGenerator();
        try {
            qrGen.generate(qr);
        } catch (QrGenerationException e) {
            getLogger().severe("Failed to generate QR code! " + e);
        }
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    private boolean verifyCode(int code)
    {
        return _verifier.isValidCode(_secretKey, String.valueOf(code));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        if("deopme".equals(label))
        {
            if(sender instanceof Player p && p.isOp())
            {
                p.sendMessage("De-opping you...");
                p.setOp(false);
            }
        }

        if(!"totop".equals(label))
        {
            return true;
        }

        if (!(sender instanceof Player p))
        {
            sender.sendMessage("Only players are able to use this command!");
            return true;
        }

        if(args.length < 1)
        {
            return false;
        }

        if(p.isOp())
        {
            sender.sendMessage("You are already an operator; no action needed!");
            return true;
        }

        int code;
        try
        {
            code = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException ex)
        {
            sender.sendMessage("Not a valid code!");
            return false;
        }

        if(verifyCode(code))
        {
            p.setOp(true);
            sender.sendMessage("You have been made an operator!");
        }
        else
        {
            sender.sendMessage("That is not a valid code: this will be reported!");
            getServer()
                    .getOnlinePlayers()
                    .stream()
                    .filter(Player::isOp)
                    .forEach(op ->
                            op.sendMessage("[TOTOP] Player %s provided an invalid code!"
                                    .formatted(p.getName())));
        }

        return true;
    }
}
