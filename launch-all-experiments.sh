rm config.properties

export CURRENT_EXPERIMENT=no-$1-max-thr

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=false
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=0" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=no-$1-max-latency

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=false
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=0" > config.properties 

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_1

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=1" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_1

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=1" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_2

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=2" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_2

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=2" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_3

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=3" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_3

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=3" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_4

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=4" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_4

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=4" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_5

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=5" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_5

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=5" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_6

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=6" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_6

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=6" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_7

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=7" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_7

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=7" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_8

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=8" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_8

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=8" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_9

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=9" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_9

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=9" > config.properties

./launch-experiment.sh

export CURRENT_EXPERIMENT=$1-max-thr_10

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=true
bufferTimeout=-1
nPastCond=10" > config.properties

./launch-experiment.sh

rm config.properties

#min=$(tail -n 1 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt)
#max=$(tail -n 2 /home/ubuntu/experiments/$CURRENT_EXPERIMENT/throughput.txt | head -1)

export CURRENT_EXPERIMENT=$1-max-latency_10

echo \
"timestampServerIp=192.168.1.31
pathToResultFolder=/home/ubuntu/experiments/$CURRENT_EXPERIMENT
timestampServerPort=4444
privacyOn=true
topologyParallelism=8
minIntervalBetweenTransactions=0
maxIntervalBetweenTransactions=1
nTransactions=300000
nDataSubject=16
minIntervalBetweenContextSwitch=1000
maxIntervalBetweenContextSwitch=3000
nContextSwitch=1
isNanoSeconds=false
bufferTimeout=10
nPastCond=10" > config.properties

./launch-experiment.sh