#!/bin/bash

exec 3> $1

for ((i=0; i < $2; i++)); do
    echo "peperoni " >&3
done

echo "Waiting for extra orders>"
cat >&3
    
exec 3>&-
