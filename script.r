library(ggplot2)
baseline1<-read.table("baseline1.txt",header=TRUE)
baseline2<-read.table("baseline2.txt",header=TRUE)
baseline3<-read.table("baseline3.txt",header=TRUE)
baseline<-rbind(baseline1,baseline2,baseline3)
baseline<-baseline[!duplicated(baseline[,1]), ]
freetime1<-read.table("freetime1.txt",header=TRUE)
freetime2<-read.table("freetime2.txt",header=TRUE)
freetime3<-read.table("freetime3.txt",header=TRUE)
freetime<-rbind(freetime1,freetime2,freetime3)
freetime<-freetime[!duplicated(freetime[,1]), ]
workload1<-read.table("workload1.txt",header=TRUE)
workload2<-read.table("workload2.txt",header=TRUE)
workload3<-read.table("workload3.txt",header=TRUE)
workload<-rbind(workload1,workload2,workload3)
workload<-workload[!duplicated(workload[,1]), ]
rel_freetime<-freetime$sum/baseline$sum[1]
rel_workload<-workload$sum/baseline$sum[1]
rel_baseline<-baseline$sum/baseline$sum[1]
g_range <- range(0, rel_freetime,rel_baseline, rel_workload)
x_range <- range(0,length(rel_baseline)-1,length(rel_freetime)-1,length(rel_workload)-1)
pdf("relative_comparisson.pdf")
plot(x=c(0:(length(rel_baseline)-1)),y=rel_baseline, type="o",col="black",xlim=x_range, ylim=g_range,ann=FALSE)
lines(x=c(0:(length(rel_workload)-1)),y=rel_workload, type="o",col="red")
lines(x=c(0:(length(rel_freetime)-1)),y=rel_freetime,type="o",col="blue")
title(main="Relative comparisson")
title(xlab="Number of failures")
title(ylab="Relative cost")
legend(1,g_range[2],c("waiting time heuristic", "workload heuristic", "baseline"),cex=0.8,col=c("blue","red","black"),pch=21,lty=1)
dev.off()
pdf("baseline.pdf")
ggplot(baseline, aes(x = factor(amountoffailures), y = cost)) +
geom_bar(position = position_dodge()) +
geom_errorbar(aes(ymin=cost-std, ymax=cost+std))
dev.off()
pdf("workload.pdf")
ggplot(workload, aes(x = factor(amountoffailures), y = cost)) +
    geom_bar(position = position_dodge()) +
    geom_errorbar(aes(ymin=cost-std, ymax=cost+std))
dev.off()
pdf("freetime.pdf")
ggplot(freetime, aes(x = factor(amountoffailures), y = cost)) +
   geom_bar(position = position_dodge()) +
   geom_errorbar(aes(ymin=cost-std, ymax=cost+std))
dev.off()