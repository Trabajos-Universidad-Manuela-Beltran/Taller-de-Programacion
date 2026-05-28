"""
Ejercicio 4 - Generador de Certificados Autofirmados
======================================================
Taller de Programación con Sockets - Ingeniería de Software

DESCRIPCIÓN:
    Genera un certificado autofirmado (server.crt) y su clave privada (server.key)
    usando el módulo cryptography de Python.
    Si no está disponible, cae en el método alternativo con subprocess + openssl.

CÓMO EJECUTAR:
    python generar_certificado.py

    Esto crea en la misma carpeta:
        server.key  → clave privada RSA 2048 bits
        server.crt  → certificado X.509 autofirmado (válido 1 año)
"""

import subprocess
import sys
import os

CERT_FILE = "server.crt"
KEY_FILE  = "server.key"


def generar_con_cryptography() -> bool:
    """Genera el certificado usando la librería cryptography (pip install cryptography)."""
    try:
        from cryptography import x509
        from cryptography.x509.oid import NameOID
        from cryptography.hazmat.primitives import hashes, serialization
        from cryptography.hazmat.primitives.asymmetric import rsa
        import datetime

        print("[*] Generando certificado con librería 'cryptography'...")

        # 1. Generar clave privada RSA 2048 bits
        clave = rsa.generate_private_key(public_exponent=65537, key_size=2048)

        # 2. Construir el certificado
        sujeto = emisor = x509.Name([
            x509.NameAttribute(NameOID.COUNTRY_NAME,             "CO"),
            x509.NameAttribute(NameOID.STATE_OR_PROVINCE_NAME,   "Bogota"),
            x509.NameAttribute(NameOID.LOCALITY_NAME,            "Bogota"),
            x509.NameAttribute(NameOID.ORGANIZATION_NAME,        "Ingenieria de Software"),
            x509.NameAttribute(NameOID.COMMON_NAME,              "localhost"),
        ])

        cert = (
            x509.CertificateBuilder()
            .subject_name(sujeto)
            .issuer_name(emisor)
            .public_key(clave.public_key())
            .serial_number(x509.random_serial_number())
            .not_valid_before(datetime.datetime.utcnow())
            .not_valid_after(datetime.datetime.utcnow() + datetime.timedelta(days=365))
            .add_extension(
                x509.SubjectAlternativeName([x509.DNSName("localhost")]),
                critical=False
            )
            .sign(clave, hashes.SHA256())
        )

        # 3. Guardar la clave privada
        with open(KEY_FILE, "wb") as f:
            f.write(clave.private_bytes(
                encoding=serialization.Encoding.PEM,
                format=serialization.PrivateFormat.TraditionalOpenSSL,
                encryption_algorithm=serialization.NoEncryption()
            ))

        # 4. Guardar el certificado
        with open(CERT_FILE, "wb") as f:
            f.write(cert.public_bytes(serialization.Encoding.PEM))

        return True

    except ImportError:
        return False


def generar_con_openssl() -> bool:
    """Fallback: genera el certificado usando el comando openssl del sistema."""
    try:
        print("[*] Generando certificado con openssl (comando del sistema)...")
        subprocess.run([
            "openssl", "req", "-x509", "-newkey", "rsa:2048",
            "-keyout", KEY_FILE,
            "-out",    CERT_FILE,
            "-days",   "365",
            "-nodes",
            "-subj",   "/C=CO/ST=Bogota/L=Bogota/O=IngSoftware/CN=localhost"
        ], check=True, capture_output=True)
        return True
    except (subprocess.CalledProcessError, FileNotFoundError):
        return False


def main() -> None:
    if os.path.exists(CERT_FILE) and os.path.exists(KEY_FILE):
        print(f"[!] Ya existen {CERT_FILE} y {KEY_FILE}. Nada que hacer.")
        return

    if generar_con_cryptography():
        print(f"[+] Certificado generado exitosamente:")
        print(f"    Clave privada : {KEY_FILE}")
        print(f"    Certificado   : {CERT_FILE}")
    elif generar_con_openssl():
        print(f"[+] Certificado generado con openssl.")
    else:
        print("[!] No se pudo generar el certificado automáticamente.")
        print("    Instala la librería: pip install cryptography")
        print("    O ejecuta manualmente:")
        print(f"    openssl req -x509 -newkey rsa:2048 -keyout {KEY_FILE} "
              f"-out {CERT_FILE} -days 365 -nodes "
              f"-subj \"/CN=localhost\"")
        sys.exit(1)


if __name__ == "__main__":
    main()
