
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
clusterId 集群 id false integer(int64)
clusterName 集群名称 false string
computerOu
域用户类
型桌面
池，
windows
操作系统
的虚拟机
模板，部
署虚拟机
时必须配
置 OU，这
里为默认
值
false string
computerType
计算机类
型：0 虚
拟机, 1
物理机 2
胖终端 3
VOI
false integer(int32)
cvmIp
用于唯一
标识不同
workspace
平台的桌
面池
false string
dataDiskSize
IDV/VOI
数据盘大
小,单位
GB
false integer(int64)
defaultStorage 系统盘 false string
description 桌面池描
述 false string
desktopNamePre 桌面名称
前缀 false string
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
desktopPoolGroupId 桌面池分
组 id false integer(int64)
desktopPoolGroupName 桌面池分
组名称 false string
desktopUserLdap 桌面加入
的域控 id false integer(int64)
domainUserPassword
部署虚拟
机时，为
虚拟机创
建的管理
员的密码
false string
domainUserPsd
部署虚拟
机时，为
虚拟机创
建的管理
员的密码
false string
domainUsername
部署虚拟
机时，为
虚拟机创
建的管理
员
false string
downStream
带宽管理
的下行带
宽数值，
单位为
kbps。范
围为 0-
100000000
false string
fileUuid
用于文件
下发时的
唯一标识
值
false string
fixedPwd 固定密码 false string
hostIdList 主机 id false array integer
hostNameList 主机名称 false array string
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
id 桌面池 id false integer(int64)
initType
初始化类
型，0 快
速初始
化，1 完
全初始化
false integer(int32)
intranetIpsCount
内网地址
列表。选
填。目前
仅支持
IPv4 地
址。
false integer(int32)
isAutoInstallation
是否开启
开启软件
驱动自动
安装
false boolean
isDataDiskModified
是否应用
数据盘大
小到终端
false integer(int32)
ldapId 域控服务
器 ID false integer(int64)
maxVmNum 最大虚拟
机数量 false integer(int32)
name 桌面池名
称 false string
noAllocationNum 未分配个
数 false integer(int32)
offlineDate
离线使用
时间，0
无限期，
其他值如
5，表示还
可以使用
5 天
false integer(int32)
onlineNumber 已接入个
数 false integer(int32)
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
ou
域用户类
型可以保
存部署虚
拟机所在
的 OU
false string
pausedNum
暂停状态
虚拟机数
量
false integer(int32)
projectId 租户 id，
云上使用 false string
publishStatus
发布状
态,1：立
即发布，
2：灰度发
布，默认
为 1
false integer(int32)
pwdOption
单点登录
密码选
项。 1.默
认使用授
权用户密
码。2.使
用固定登
录密码，
不设置固
定密码视
为无密码
false integer(int32)
recoverMode
0 不还原,
1 全盘还
原，2 系
统盘还
原 ；胖终
端类型才
会用到此
字段
false integer(int32)
runningNum
运行状态
虚拟机数
量
false integer(int32)
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
secondStoragePoolName 第二存储
池名称 false string
shutOffNum
关机状态
虚拟机数
量
false integer(int32)
startSerial
序号起始
值，用于
部分对于
计算机名
有自定义
要求的局
点
false integer(int64)
storagePoolName 存储池名
称 false string
targetType
部署目标
类型，0
集群，1
主机
false integer(int32)
templateUuid 桌面镜像
UUID false string
terminalType 胖终端设
备类型 false string
unknownNum
未知状态
虚拟机数
量
false integer(int32)
upStream
带宽管理
的上行带
宽数值，
单位为
kbps。范
围为 0-
100000000
false string
userType
用户类
型，0:本
地用户，
1:域用
户，2：设
备用户 3:
false integer(int32)
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
统信域用
户 8:麒麟
天御
vmNum 虚拟机个
数 false integer(int32)
vmOnlinePercent
在线率 -
仅在大屏
展示统计
临时变量
false integer(int32)
vmTemplateId 虚拟机模
板 id false integer(int64)
vmUsedPercent 虚拟桌面
利用率 false integer(int32)
voiCreateRestorePoint
是否开启
创建还原
点
false boolean
winServerStrategy
虚拟应用
池用的一
些策略
false WinServerStrategy WinServerStrategy
disableCmd
禁用命令
提示符：
0-不禁
用，1-禁
用并同时
禁止脚本
运行，2-
只禁用
cmd.exe
false integer
disableControlPanel
禁用控制
面板：0-
不禁用，
1-禁用
false integer
disableManageMyComputerVerb
禁用管
理：0-不
禁用，1-
禁用
false integer
disableNetConnectDisconnect 禁用“映
射网络驱 false integer
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
动器”和
“断开网
络驱动
器”：0-
不禁用，
1-禁用
disableRegistry
禁用注册
表：0-不
禁用，1-
禁用
false integer
disableTaskMgr
禁用任务
管理器：
0-不禁
用，1-禁
用
false integer
hideLocalDisk
仅限制 A
B :2 仅限
制 C :3
仅限制
D :4 仅限
制 A B
C :5 仅限
制 A B C
D: 6 限制
所有驱动
器: 1 不
限制驱动
器：0
false integer
hideRecycleBin
隐藏回收
站：0-显
示,1-影藏
false integer
id 主键 id false integer
sessionDisconnect false boolean
sessionDisconnectTime false integer
sessionLogout false boolean
参数名称 参数说明
请
求
类
型
必须 数据类型 schema
sessionLogoutTime false integer
响应状态
状态码 说明 schema
200 OK RpcResult«long»
201 Created
401 Unauthorized
403 Forbidden
404 Not Found
响应参数
参数名称 参数说明 类型 schema
data integer(int64) integer(int64)
errorCode 错误码 integer(int32) integer(int32)
failureMessage 操作失败消息，仅失败时返回 string
state 本次请求状态 integer(int32) integer(int32)
success 执行是否成功，0：成功，1：失败，2：部分
成功 boolean
successMessage 操作成功消息,仅成功时返回 string
响应示例
{
"data": 0,
"errorCode": 0,
"failureMessage": "操作失败",
"state": 0,
"success": true,
"successMessage": "操作成功"
}