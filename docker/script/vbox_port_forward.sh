# forward all calls to host port 5000 to a vbox machned named dev port 5000
# need to run once only
export vm_name='dev'

vboxmanage controlvm $vm_name poweroff
vboxmanage modifyvm $vm_name --natpf1 "api,tcp,,5000,,5000"
