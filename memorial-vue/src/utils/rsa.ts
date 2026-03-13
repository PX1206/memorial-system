// src/utils/rsa.ts
import JSEncrypt from 'jsencrypt';

const PUBLIC_KEY = `-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBL+MR/KDuMRq/fgYWuBdESgo
sWAqVeVw1XYgH2+H02rW22JJyv1ncia1AzYOn3sSH0Pq5wtzUbK5T1DHcOHxvzT+
IgOsg6COulYMcMDX1jRMB95g/jOzyrkmHVVqjA5DNCr9O/9WWXc56v/cCOsIEXzA
e7U0fv1vaYsjkTTR+0wIDAQAB
-----END PUBLIC KEY-----`;

export function encryptPassword(password: string): string {
  const encrypt = new JSEncrypt();
  encrypt.setPublicKey(PUBLIC_KEY);
  const encrypted = encrypt.encrypt(password);
  if (!encrypted) {
    throw new Error('RSA 加密失败');
  }
  return encrypted;
}
