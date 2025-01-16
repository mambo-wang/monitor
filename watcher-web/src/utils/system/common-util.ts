import gmcrypt from "gm-crypt";

const SM4 = gmcrypt.sm4;
const KEY = "*yoiH&^%56_Ha!@#";
const encryptBySm4 = (message: any) => {
    if (message === undefined || message === null) {
        return message;
    }
    const sm4Config = {
        key: KEY,
        mode: "ecb",
        cipherType: "base64",
    };
    const encrypt = new SM4(sm4Config);
    const pwd = encrypt.encrypt(message);
    return pwd.toString();
};
const decryptBySm4 = (ciphertext: any) => {
    if (ciphertext === undefined || ciphertext === null) {
        return ciphertext;
    }
    const sm4Config = {
        key: KEY,
        mode: "ecb",
        cipherType: "base64",
    };
    const decrypt = new SM4(sm4Config);
    const pwd = decrypt.decrypt(ciphertext);
    return pwd.toString();
};

export default {
    maskList: [
        "128.0.0.0",
        "192.0.0.0",
        "224.0.0.0",
        "240.0.0.0",
        "248.0.0.0",
        "252.0.0.0",
        "254.0.0.0",
        "255.0.0.0",
        "255.128.0.0",
        "255.192.0.0",
        "255.224.0.0",
        "255.240.0.0",
        "255.248.0.0",
        "255.252.0.0",
        "255.254.0.0",
        "255.255.0.0",
        "255.255.128.0",
        "255.255.192.0",
        "255.255.224.0",
        "255.255.240.0",
        "255.255.248.0",
        "255.255.252.0",
        "255.255.254.0",
        "255.255.255.0",
        "255.255.255.128",
        "255.255.255.192",
        "255.255.255.224",
        "255.255.255.240",
        "255.255.255.248",
        "255.255.255.252",
        "255.255.255.254",
        "255.255.255.255",
    ],
    ipReg:
        /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/,
    portReg:
        /^([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{4}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
    domainReg: /^(?=^.{3,256}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$/,
    encryptBySm4,
    decryptBySm4
};
