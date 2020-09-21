FROM docker.io/library/centos:centos6.10
ARG TMP_RPM="/tmp/rpm"
RUN mkdir ${TMP_RPM} && \
  rpm --initdb --dbpath ${TMP_RPM} && \
  yum remove setup -y -q && \
  yum install --downloadonly --downloaddir=/tmp setup -y -q && \
  cd ${TMP_RPM} && \
  rpm --dbpath ${TMP_RPM} -i ../setup-*.noarch.rpm

FROM scratch
COPY --from=0 /tmp/rpm/Packages /var/lib/rpm/Packages
COPY --from=0 /etc/centos-release /etc/os-release
COPY --from=0 /etc/centos-release /etc/centos-release
