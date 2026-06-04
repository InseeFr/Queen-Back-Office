#!/bin/bash
# Fix: Docker Desktop bloque le trafic inter-conteneurs Podman
#
# Cause : Docker Desktop impose "policy drop" sur la chaîne nftables FORWARD
# dans la VM WSL2 partagée, et ne whitteliste que ses propres interfaces
# (docker0, br-*). Le bridge Podman (podman1) est donc bloqué.
#
# Usage:
#   Fix immédiat (perdu au redémarrage de la VM) :
#     bash podman-nft-fix.sh immediate
#
#   Fix persistant (service systemd dans la VM) :
#     bash podman-nft-fix.sh persistent
#
#   Vérification :
#     bash podman-nft-fix.sh check

set -e

case "${1:-}" in
  immediate)
    echo "Application du fix immédiat..."
    podman machine ssh "nft insert rule ip filter FORWARD iifname 'podman1' accept; nft insert rule ip filter FORWARD oifname 'podman1' accept"
    echo "OK - règles ajoutées."
    ;;

  persistent)
    echo "Installation du service systemd dans la VM Podman..."
    podman machine ssh "cat > /etc/systemd/system/podman-nft-fix.service << 'EOF'
[Unit]
Description=Allow Podman bridge traffic (fix Docker Desktop nftables policy drop)
After=network.target

[Service]
Type=oneshot
ExecStart=/bin/sh -c \"nft insert rule ip filter FORWARD iifname 'podman1' accept; nft insert rule ip filter FORWARD oifname 'podman1' accept\"
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF
systemctl daemon-reload && systemctl enable podman-nft-fix.service"
    echo "OK - service activé."
    ;;

  check)
    echo "=== Règles nftables FORWARD ==="
    podman machine ssh "nft list chain ip filter FORWARD"
    echo ""
    echo "=== Connectivité inter-conteneurs ==="
    podman exec activemq bash -c "timeout 3 bash -c 'echo > /dev/tcp/questionnaire-sabiane-db/5432' && echo 'OK' || echo 'FAIL'" 2>&1
    ;;

  *)
    echo "Usage: $0 {immediate|persistent|check}"
    exit 1
    ;;
esac
