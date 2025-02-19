<?php
function generateOtp($secretKey)
{
    date_default_timezone_set("UTC"); // 确保时区一致
    $minutes = floor(time() / 60); // 当前时间戳按分钟取整

    // 拼接密钥和分钟数
    $input = $secretKey . $minutes;

    // 计算 SHA-256 哈希
    $hash = hash("sha256", $input, true);

    // 取前 8 个字节，并转换为 8 位纯数字 OTP
    $otp = "";
    for ($i = 0; $i < 8; $i++) {
        $otp .= ord($hash[$i]) % 10; // 取 0-9 的数字
    }

    return $otp;
}

$otp = "";
if ($_SERVER["REQUEST_METHOD"] == "POST" && !empty($_POST["secretKey"])) {
    $otp = generateOtp($_POST["secretKey"]);
}
?>

<!DOCTYPE html>
<html lang="zh">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OTP Generatore</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f4f4f4;
            margin: 0;
        }

        .container {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        input,
        button {
            margin-top: 10px;
            padding: 10px;
            width: 100%;
            font-size: 16px;
        }

        .otp {
            font-size: 24px;
            font-weight: bold;
            color: #007bff;
            margin-top: 20px;
        }

        .copy-btn {
            background-color: #28a745;
            color: white;
            border: none;
            cursor: pointer;
        }
    </style>
</head>

<body>

    <div class="container">
        <h2>OTP Generatore</h2>
        <form method="POST">
            <input type="text" name="secretKey" placeholder="Inserisci Secret Key" required>
            <button type="submit">Genera OTP</button>
        </form>

        <?php if (!empty($otp)): ?>
            <div class="otp">OTP: <span id="otp"><?php echo $otp; ?></span></div>
            <button class="copy-btn" onclick="copyToClipboard()">Copia OTP</button>
        <?php endif; ?>
    </div>

    <script>
        function copyToClipboard() {
            var otpText = document.getElementById("otp").innerText;
            navigator.clipboard.writeText(otpText).then(function () {
                alert("OTP Copiato: " + otpText);
            }, function () {
                alert("Errore,Prova copiare munualmemte");
            });
        }
    </script>

</body>

</html>