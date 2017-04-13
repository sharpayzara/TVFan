
---------------------- bit utils
bit={data32={}}
for i=1,32 do
    bit.data32[i]=2^(32-i)
end

function bit.d2b(arg)
    local   tr={}
    for i=1,32 do
        if arg >= bit.data32[i] then
        tr[i]=1
        arg=arg-bit.data32[i]
        else
        tr[i]=0
        end
    end
    return   tr
end   --bit:d2b

function    bit.b2d(arg)
    local   nr=0
    for i=1,32 do
        if arg[i] ==1 then
        nr=nr+2^(32-i)
        end
    end
    return  nr
end   --bit:b2d

function    bit._xor(a,b)
    local   op1=bit.d2b(a)
    local   op2=bit.d2b(b)
    local   r={}

    for i=1,32 do
        if op1[i]==op2[i] then
            r[i]=0
        else
            r[i]=1
        end
    end
    return  bit.b2d(r)
end --bit:xor

function    bit._and(a,b)
    local   op1=bit.d2b(a)
    local   op2=bit.d2b(b)
    local   r={}
    
    for i=1,32 do
        if op1[i]==1 and op2[i]==1  then
            r[i]=1
        else
            r[i]=0
        end
    end
    return  bit.b2d(r)
    
end --bit:_and

function    bit._or(a,b)
    local   op1=bit.d2b(a)
    local   op2=bit.d2b(b)
    local   r={}
    
    for i=1,32 do
        if  op1[i]==1 or   op2[i]==1   then
            r[i]=1
        else
            r[i]=0
        end
    end
    return  bit.b2d(r)
end --bit:_or

function    bit._not(a)
    local   op1=bit.d2b(a)
    local   r={}

    for i=1,32 do
        if  op1[i]==1   then
            r[i]=0
        else
            r[i]=1
        end
    end
    return  bit.b2d(r)
end --bit:_not

function    bit._rshift(a,n)
    local   op1=bit.d2b(a)
    local   r=bit.d2b(0)
    
    if n < 32 and n > 0 then
        for i=1,n do
            for i=31,1,-1 do
                op1[i+1]=op1[i]
            end
            op1[1]=0
        end
    r=op1
    end
    return  bit.b2d(r)
end --bit:_rshift

function    bit._lshift(a,n)
    local   op1=bit.d2b(a)
    local   r=bit.d2b(0)
    
    if n < 32 and n > 0 then
        for i=1,n   do
            for i=1,31 do
                op1[i]=op1[i+1]
            end
            op1[32]=0
        end
    r=op1
    end
    return  bit.b2d(r)
end --bit:_lshift

function    bit.print(ta)
    local   sr=""
    for i=1,32 do
        sr=sr..ta[i]
    end
    print(sr)
end




-------------------------------- core
i_key = {1282092843, 3240703931, 2366342603, 1359558529}


function getConfused(flag)
	local ftr = math.log10(1000);
	local ftr2 = math.log10(100);
	local a = math.max(i_key[1], ftr);
	local b = math.min(i_key[2], ftr2);
	local c = math.max(i_key[3], ftr);
	local d = math.min(i_key[4], ftr2);
	return a, b, c, d; 
end


function doDec(ciphertxt, len, flag)
	dc = luajava.newInstance("com.decapi.Decryptions");
	local L, l, R, r = getConfused(flag);
	ret = dc:AESDec(ciphertxt, len, math.btan2(L, l, R, r));
	return ret;
end

function doEnc(plaintxt, flag)
	ec = luajava.newInstance("com.decapi.Decryptions");
	local L, l, R, r = getConfused(flag);
	ret = ec:AESEnc(plaintxt, math.btan2(L, l, R, r));
	return ret;
end