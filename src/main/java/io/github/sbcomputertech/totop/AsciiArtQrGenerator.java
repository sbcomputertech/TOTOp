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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import xyz.dunjiao.cloud.commons.lang.QRCodeUtils;

public class AsciiArtQrGenerator implements QrGenerator {
    @Override
    public String getImageMimeType() {
        return "text/plain";
    }

    @Override
    public byte[] generate(QrData qrData) throws QrGenerationException {
        String uri = qrData.getUri();
        QRCodeWriter writer = new QRCodeWriter();

        BitMatrix bitMatrix;
        try {
            bitMatrix = writer.encode(uri, BarcodeFormat.QR_CODE, 64, 64);
        } catch (WriterException ex) {
            throw new QrGenerationException("Failed to encode QR", ex);
        }

        //TODO: Spigot complains about STDOUT over plugin logger -> fix
        QRCodeUtils.print(bitMatrix, false);
        return new byte[0];
    }
}
