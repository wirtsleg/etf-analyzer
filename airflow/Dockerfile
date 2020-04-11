FROM puckel/docker-airflow:1.10.9

USER root
RUN groupadd --gid 999 docker \
    && usermod -aG docker airflow
USER airflow
