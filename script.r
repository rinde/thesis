baseline<-read.table("baseline.txt",header=TRUE)
freetime<-read.table("freetime.txt",header=TRUE)
workload<-read.table("workload.txt",header=TRUE)
rel_freetime<-freetime$sum/baseline$sum[1]
rel_workload<-workload$sum/baseline$sum[1]
rel_baseline<-baseline$sum/baseline$sum[1]
g_range <- range(0, rel_freetime,rel_baseline, rel_workload)
pdf("relative_comparisson.pdf")
plot(x=c(0:(length(rel_baseline)-1)),y=rel_baseline, type="o",col="black", ylim=g_range,ann=FALSE)
lines(x=c(0:(length(rel_baseline)-1)),y=rel_workload, type="o",col="red")
lines(x=c(0:(length(rel_baseline)-1)),y=rel_freetime,type="o",col="blue")
title(main="Relative comparisson")
title(xlab="Number of failures")
title(ylab="Relative cost")
legend(1,g_range[2],c("waiting time heuristic", "workload heuristic", "baseline"),cex=0.8,col=c("blue","red","black"),pch=21,lty=1)
dev.off()