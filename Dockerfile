FROM apacheignite/ignite:2.6.0

COPY ./demo-cache.config.xml $IGNITE_HOME/config/.

ENV CONFIG_URI $IGNITE_HOME/config/demo-cache.config.xml